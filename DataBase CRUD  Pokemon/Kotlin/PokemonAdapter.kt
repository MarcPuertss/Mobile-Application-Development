package com.marcpuertas.pokemonbd

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PokemonAdapter(
    private val pokemonList: ArrayList<Card>,
    private val editClickListener: (Card) -> Unit,
    private val viewClickListener: (Card) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.pokemonNameTextView)
        val tiposTextView: TextView = itemView.findViewById(R.id.pokemonTypeTextView)
        val imageView: ImageView = itemView.findViewById(R.id.pokemonImageView)
        val imageEdit: ImageView = itemView.findViewById(R.id.btnEdit)

        init {
            itemView.setOnClickListener {
                viewClickListener(pokemonList[adapterPosition])
            }

            imageEdit.setOnClickListener {
                editClickListener(pokemonList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val currentPokemon = pokemonList[position]
        holder.nameTextView.text = currentPokemon.name
        holder.tiposTextView.text = currentPokemon.tipos
        // Asignar el Bitmap a la ImageView
        holder.imageView.setImageBitmap(currentPokemon.image)
    }

    override fun getItemCount(): Int {
        return pokemonList.size
    }
}
