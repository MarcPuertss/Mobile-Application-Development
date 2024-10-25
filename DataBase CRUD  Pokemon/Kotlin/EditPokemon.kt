package com.marcpuertas.pokemonbd

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream

class EditPokemon : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtHealth: EditText
    private lateinit var edtTipos: EditText
    private lateinit var edtNivel: EditText
    private lateinit var edtPokeball: EditText
    private lateinit var imgPokemon: ImageView
    private lateinit var btnUpdate: Button
    private var selectedImageBitmap: Bitmap? = null
    private var pokemonId: Int = -1
    private lateinit var goBack: FloatingActionButton

    // Variables para guardar los valores actuales
    private var currentName: String? = null
    private var currentHealth: Int = 0
    private var currentTipos: String? = null
    private var currentNivel: Int = 0
    private var currentPokeball: String? = null
    private var currentImageBytes: ByteArray? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pokemon)

        edtName = findViewById(R.id.nameEditText)
        edtHealth = findViewById(R.id.healthEditText)
        edtTipos = findViewById(R.id.typeEditText)
        edtNivel = findViewById(R.id.levelEditText)
        edtPokeball = findViewById(R.id.pokeballEditText)
        imgPokemon = findViewById(R.id.pokemonImageView)
        btnUpdate = findViewById(R.id.btnUpdate)
        goBack = findViewById(R.id.backbtn)

        // Obtener los datos actuales del Pokémon desde el Intent
        pokemonId = intent.getIntExtra("id", -1)
        currentName = intent.getStringExtra("name")
        currentHealth = intent.getIntExtra("health", 0)
        currentTipos = intent.getStringExtra("tipos")
        currentNivel = intent.getIntExtra("nivel", 0)
        currentPokeball = intent.getStringExtra("pokeball")
        currentImageBytes = intent.getByteArrayExtra("image")

        // Pre-cargar los datos en los campos de edición
        edtName.setText(currentName)
        edtHealth.setText(currentHealth.toString())
        edtTipos.setText(currentTipos)
        edtNivel.setText(currentNivel.toString())
        edtPokeball.setText(currentPokeball)

        // Mostrar la imagen si existe
        if (currentImageBytes != null && currentImageBytes!!.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(currentImageBytes, 0, currentImageBytes!!.size)
            imgPokemon.setImageBitmap(bitmap)
        } else {
            imgPokemon.setImageResource(R.drawable.bmw) // Imagen por defecto
        }

        imgPokemon.setOnClickListener { pickImageFromGallery() }
        btnUpdate.setOnClickListener { updatePokemon() }
        goBack.setOnClickListener { gooback() }
    }

    private fun gooback(){
        finish()
    }



    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, MainActivity.REQUEST_CODE_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MainActivity.REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
                    imgPokemon.setImageBitmap(resizedBitmap)
                    selectedImageBitmap = resizedBitmap
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al carregar l'imatge", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updatePokemon() {
        // Obtener los valores actuales de los campos
        val updatedName = edtName.text.toString()
        val updatedHealth = edtHealth.text.toString().toIntOrNull() ?: currentHealth
        val updatedTipos = edtTipos.text.toString()
        val updatedNivel = edtNivel.text.toString().toIntOrNull() ?: currentNivel
        val updatedPokeball = edtPokeball.text.toString()

        // Comparar los valores actuales con los originales
        val nameToSave = if (updatedName != currentName) updatedName else currentName
        val healthToSave = if (updatedHealth != currentHealth) updatedHealth else currentHealth
        val tiposToSave = if (updatedTipos != currentTipos) updatedTipos else currentTipos
        val nivelToSave = if (updatedNivel != currentNivel) updatedNivel else currentNivel
        val pokeballToSave = if (updatedPokeball != currentPokeball) updatedPokeball else currentPokeball
        val imageBytes = if (selectedImageBitmap != null) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            selectedImageBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        } else {
            currentImageBytes
        }

        if (nameToSave!!.isNotEmpty() && tiposToSave!!.isNotEmpty()) {
            val intent = Intent().apply {
                putExtra("id", pokemonId)
                putExtra("name", nameToSave)
                putExtra("health", healthToSave)
                putExtra("tipos", tiposToSave)
                putExtra("nivel", nivelToSave)
                putExtra("pokeball", pokeballToSave)
                putExtra("imageBytes", imageBytes) // Guardar la imagen actual o modificada
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "Si us plau, omple tots els camps ", Toast.LENGTH_SHORT).show()
        }
    }
}
