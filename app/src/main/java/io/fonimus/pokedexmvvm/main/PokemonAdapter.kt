package io.fonimus.pokedexmvvm.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.fonimus.pokedexmvvm.R

class PokemonAdapter(private val onClickEvent: (PokemonViewState) -> Unit) :
    ListAdapter<PokemonViewState, PokemonViewHolder>(PokemonDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PokemonViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.pokemon_itemview, parent, false)
        )

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(getItem(position), onClickEvent)
    }

}

object PokemonDiffUtil : DiffUtil.ItemCallback<PokemonViewState>() {
    override fun areItemsTheSame(oldItem: PokemonViewState, newItem: PokemonViewState) =
        oldItem.pokemonNumber == newItem.pokemonNumber

    override fun areContentsTheSame(oldItem: PokemonViewState, newItem: PokemonViewState) =
        oldItem == newItem
}

class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val pokemonCard: CardView = itemView.findViewById(R.id.pokemon_card)
    private val pokemonNumberTextView: TextView = itemView.findViewById(R.id.pokemon_number)
    private val pokemonNameTextView: TextView = itemView.findViewById(R.id.pokemon_name)
    private val pokemonImageView: ImageView = itemView.findViewById(R.id.pokemon_image)

    fun bind(state: PokemonViewState, onClickEvent: (PokemonViewState) -> Unit) {
        pokemonCard.setOnClickListener { onClickEvent(state) }
        pokemonNumberTextView.text = state.pokemonNumber
        pokemonNameTextView.text = state.pokemonName
        Glide.with(pokemonImageView).load(state.pokemonImageUrl)
            .fitCenter()
            .into(pokemonImageView)
    }

}
