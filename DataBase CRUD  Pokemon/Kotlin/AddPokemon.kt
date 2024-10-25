package com.marcpuertas.pokemonbd

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream

class AddPokemon : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtHealth: EditText
    private lateinit var edtTipos: EditText
    private lateinit var edtNivel: EditText
    private lateinit var spnPokeball: EditText
    private lateinit var imgPokemon: ImageView
    private lateinit var btnAdd: Button
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var goBack: FloatingActionButton


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pokemon)

        edtName = findViewById(R.id.edtName)
        edtHealth = findViewById(R.id.edtHealth)
        edtTipos = findViewById(R.id.edtTipos)
        edtNivel = findViewById(R.id.edtNivel)
        spnPokeball = findViewById(R.id.spnPokeball)
        imgPokemon = findViewById(R.id.imgPokemon)
        btnAdd = findViewById(R.id.btnAdd)
        goBack = findViewById(R.id.backbtn)

        imgPokemon.setOnClickListener { pickImageFromGallery() }

        btnAdd.setOnClickListener { savePokemon() }
        goBack.setOnClickListener { gooback() }

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, MainActivity.REQUEST_CODE_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MainActivity.REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    imgPokemon.setImageBitmap(bitmap) // Mostrar la imagen seleccionada
                    selectedImageBitmap = bitmap // Guardar el bitmap seleccionado
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun gooback(){
        finish()
    }

    private fun savePokemon() {
        val name = edtName.text.toString()
        val health = edtHealth.text.toString().toIntOrNull() ?: 0
        val tipos = edtTipos.text.toString()
        val nivel = edtNivel.text.toString().toIntOrNull() ?: 0
        val pokeball = spnPokeball.text.toString()

        if (name.isNotEmpty() && tipos.isNotEmpty() && selectedImageBitmap != null) {
            // Redimensiona la imagen para que no sea tan grande
            val resizedBitmap = Bitmap.createScaledBitmap(selectedImageBitmap!!, 300, 300, true)

            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()

            val intent = Intent().apply {
                putExtra("name", name)
                putExtra("health", health)
                putExtra("tipos", tipos)
                putExtra("nivel", nivel)
                putExtra("pokeball", pokeball)
                putExtra("imageBytes", imageBytes)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "Si us plau, omple tots els camps, i selecciona una foto ", Toast.LENGTH_SHORT).show()
        }
    }


}

