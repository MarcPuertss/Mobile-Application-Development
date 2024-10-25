package com.marcpuertas.pokemonbd

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var db: SQLiteDatabase
    private lateinit var listofchars: RecyclerView
    private lateinit var lchars: ArrayList<Card>
    private lateinit var pokemonAdapter: PokemonAdapter


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listofchars = findViewById(R.id.dades)
        lchars = ArrayList()
        pokemonAdapter = PokemonAdapter(lchars, { card -> openEditPokemonActivity(card) }, { card -> openViewPokemonActivity(card) })

        listofchars.adapter = pokemonAdapter
        listofchars.layoutManager = LinearLayoutManager(this)

        getDatabase("pokemon_db")
        refreshPokemonList()

        val btnAdd: FloatingActionButton = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddPokemon::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD)
        }

    }

    private fun getDatabase(databasename: String) {
        db = openOrCreateDatabase(databasename, MODE_PRIVATE, null)
        db.execSQL(""" 
            CREATE TABLE IF NOT EXISTS pokemon (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                health INTEGER NOT NULL,
                tipos TEXT NOT NULL,
                nivel INTEGER NOT NULL,
                pokeball TEXT NOT NULL,
                image BLOB 
            )
        """.trimIndent())
    }

    fun deletePokemon(name: String) {
        db.delete("pokemon", "name = ?", arrayOf(name))
        Toast.makeText(this, "$name eliminat", Toast.LENGTH_SHORT).show()
        refreshPokemonList()
    }

    private fun addPokemon(name: String, health: Int, tipos: String, nivel: Int, pokeball: String, image: Bitmap) {
        if (pokemonExists(name)) {
            Toast.makeText(this, "El Pokémon $name ya existeix", Toast.LENGTH_SHORT).show()
            return
        }

        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        val values = ContentValues().apply {
            put("name", name)
            put("health", health)
            put("tipos", tipos)
            put("nivel", nivel)
            put("pokeball", pokeball)
            put("image", byteArray) // Almacena la imagen como BLOB
        }

        val newRowId = db.insert("pokemon", null, values)

        if (newRowId != -1L) {
            Toast.makeText(this, "$name afegit", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al afegir $name", Toast.LENGTH_SHORT).show()
        }

        refreshPokemonList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD -> {
                    val name = data?.getStringExtra("name") ?: return
                    val health = data.getIntExtra("health", 0)
                    val tipos = data.getStringExtra("tipos") ?: return
                    val nivel = data.getIntExtra("nivel", 0)
                    val pokeball = data.getStringExtra("pokeball") ?: return

                    // Verifica que el array de bytes no sea nulo
                    val imageByteArray = data.getByteArrayExtra("imageBytes")
                    if (imageByteArray != null) {
                        val imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)

                        if (name.isNotEmpty() && tipos.isNotEmpty()) {
                            addPokemon(name, health, tipos, nivel, pokeball, imageBitmap)
                        } else {
                            Toast.makeText(this, "Si us plau, omple tots els camps", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Error: Imatge no seleccionada", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_CODE_EDIT -> {
                    // Lo mismo para la edición de Pokémon
                    val id = data?.getIntExtra("id", -1) ?: return
                    val name = data.getStringExtra("name") ?: return
                    val health = data.getIntExtra("health", 0)
                    val tipos = data.getStringExtra("tipos") ?: return
                    val nivel = data.getIntExtra("nivel", 0)
                    val pokeball = data.getStringExtra("pokeball") ?: return
                    val imageByteArray = data.getByteArrayExtra("imageBytes")

                    if (imageByteArray != null) {
                        val imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                        updatePokemon(id, name, health, tipos, nivel, pokeball, imageBitmap)
                    } else {
                        Toast.makeText(this, "Error: imatge no seleccionada", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_CODE_DELETE -> {
                    val name = data?.getStringExtra("name") ?: return
                    deletePokemon(name)
                }
            }
        }
    }

    private fun updatePokemon(id: Int, name: String, health: Int, tipos: String, nivel: Int, pokeball: String, image: Bitmap) {
        if (pokemonExists(name)) {
            Toast.makeText(this, "El Pokémon $name ya existeix", Toast.LENGTH_SHORT).show()
            return
        }
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        val values = ContentValues().apply {
            put("name", name)
            put("health", health)
            put("tipos", tipos)
            put("nivel", nivel)
            put("pokeball", pokeball)
            put("image", byteArray)
        }

        db.update("pokemon", values, "id = ?", arrayOf(id.toString()))

        Toast.makeText(this, "$name actualitzat", Toast.LENGTH_SHORT).show()
        refreshPokemonList()
    }

    private fun refreshPokemonList() {
        lchars.clear()

        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM pokemon", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val health = cursor.getInt(cursor.getColumnIndexOrThrow("health"))
                    val tipos = cursor.getString(cursor.getColumnIndexOrThrow("tipos"))
                    val nivel = cursor.getInt(cursor.getColumnIndexOrThrow("nivel"))
                    val pokeball = cursor.getString(cursor.getColumnIndexOrThrow("pokeball"))

                    val imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("image"))
                    val bitmap = if (imageBlob != null) {
                        BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.size)
                    } else {
                        BitmapFactory.decodeResource(resources, R.drawable.bmw)
                    }

                    lchars.add(Card(id, name, health, tipos, nivel, pokeball, bitmap)) // Usa Bitmap en vez de String
                } while (cursor.moveToNext())
            }
            cursor.close()
            pokemonAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error al recuperar Pokémon:  ${e.message}")
        }
    }


    private fun openEditPokemonActivity(pokemon: Card) {
        val intent = Intent(this, EditPokemon::class.java)
        intent.putExtra("id", pokemon.id)
        intent.putExtra("name", pokemon.name)
        intent.putExtra("health", pokemon.health)
        intent.putExtra("tipos", pokemon.tipos)
        intent.putExtra("nivel", pokemon.nivel)
        intent.putExtra("pokeball", pokemon.pokeball)

        val stream = ByteArrayOutputStream()
        pokemon.image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        intent.putExtra("image", byteArray) // Pasa el BLOB de la imagen
        startActivityForResult(intent, REQUEST_CODE_EDIT)
    }

    private fun openViewPokemonActivity(pokemon: Card) {
        val intent = Intent(this, ViewPokemon::class.java)
        intent.putExtra("id", pokemon.id)
        intent.putExtra("name", pokemon.name)
        intent.putExtra("health", pokemon.health)
        intent.putExtra("tipos", pokemon.tipos)
        intent.putExtra("nivel", pokemon.nivel)
        intent.putExtra("pokeball", pokemon.pokeball)

        // Convertir el Bitmap a un array de bytes para pasarlo al ViewPokemon
        val stream = ByteArrayOutputStream()
        pokemon.image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        intent.putExtra("image", byteArray) // Pasa el BLOB de la imagen
        startActivityForResult(intent, REQUEST_CODE_DELETE)
    }

    private fun pokemonExists(name: String): Boolean {
        val cursor: Cursor = db.rawQuery("SELECT * FROM pokemon WHERE name = ?", arrayOf(name))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    companion object {
        const val REQUEST_CODE_ADD = 1
        const val REQUEST_CODE_EDIT = 2
        const val REQUEST_CODE_DELETE = 3
        const val REQUEST_CODE_IMAGE_PICK = 1001
    }
}
