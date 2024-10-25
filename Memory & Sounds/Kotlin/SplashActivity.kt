package com.marcpuertas.memorygame;

import android.content.Intent
import android.os.Bundle;
import android.os.Handler
import android.os.Looper


import androidx.appcompat.app.AppCompatActivity

public class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Creació de l'Activitat per mostrar la pantalla Splash i que trigui 4 segons.
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 4000)
    }
}
