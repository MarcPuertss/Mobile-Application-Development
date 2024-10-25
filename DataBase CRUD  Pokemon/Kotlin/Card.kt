package com.marcpuertas.pokemonbd

import android.graphics.Bitmap

data class Card(
    val id: Int,
    val name: String,
    val health: Int,
    val tipos: String,
    val nivel: Int,
    val pokeball: String,
    val image: Bitmap
)
