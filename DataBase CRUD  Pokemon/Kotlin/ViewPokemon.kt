package com.marcpuertas.pokemonbd

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewPokemon : AppCompatActivity() {
    private lateinit var txtName: TextView
    private lateinit var txtHealth: TextView
    private lateinit var txtTipos: TextView
    private lateinit var txtNivel: TextView
    private lateinit var txtPokeball: TextView
    private lateinit var imgPokemon: ImageView
    private lateinit var btnDelete: Button
    private var currentImageBytes: ByteArray? = null
    private lateinit var goBack: FloatingActionButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pokemon)

        // Inicializar tus elementos de la UI
        txtName = findViewById(R.id.txtName)
        txtHealth = findViewById(R.id.txtHealth)
        txtTipos = findViewById(R.id.txtTipos)
        txtNivel = findViewById(R.id.txtNivel)
        txtPokeball = findViewById(R.id.txtPokeball)
        imgPokemon = findViewById(R.id.imgPokemon)
        btnDelete = findViewById(R.id.btnDelete)
        goBack = findViewById(R.id.backbtn)

        // Obtener datos del Intent
        val name = intent.getStringExtra("name")
        val health = intent.getIntExtra("health", 0)
        val tipos = intent.getStringExtra("tipos")
        val nivel = intent.getIntExtra("nivel", 0)
        val pokeball = intent.getStringExtra("pokeball")
        currentImageBytes = intent.getByteArrayExtra("image")

        // Establecer los valores en los elementos de la interfaz
        txtName.text = name
        txtHealth.text = "Salut: $health"
        txtTipos.text = "Tipus: $tipos"
        txtNivel.text = "Nivel: $nivel"
        txtPokeball.text = "Pokeball: $pokeball"


        // Mostrar la imagen o una predeterminada si no hay imagen
        if (currentImageBytes != null && currentImageBytes!!.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(currentImageBytes, 0, currentImageBytes!!.size)
            imgPokemon.setImageBitmap(bitmap)
        } else {
            imgPokemon.setImageResource(R.drawable.bmw) // Imagen por defecto
        }

        // Configurar el botón de eliminar
        btnDelete.setOnClickListener { showDeleteConfirmationDialog(name) }
        goBack.setOnClickListener { gooback() }
    }
    private fun gooback(){
        finish()
    }


    private fun showDeleteConfirmationDialog(pokemonName: String?) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Eliminar Pokémon")
            .setMessage("¿Estás segur de que vols eliminar a $pokemonName?")
            .setPositiveButton("Sí") { _, _ -> pokemonName?.let { deletePokemon(it) } }
            .setNegativeButton("No", null)
            .create()

        dialog.show()
    }

    private fun deletePokemon(name: String) {
        val resultIntent = Intent().apply { putExtra("name", name) }
        setResult(Activity.RESULT_OK, resultIntent)
        Toast.makeText(this, "$name eliminat ", Toast.LENGTH_SHORT).show()
        finish()
    }
}
