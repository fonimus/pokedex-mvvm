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

class PokemonAdapter(
    private val onClickEvent: (PokemonViewState.Content) -> Unit,
    private val onLoadingEvent: () -> Unit
) :
    ListAdapter<PokemonViewState, RecyclerView.ViewHolder>(PokemonDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (PokemonViewState.Type.values()[viewType]) {
            PokemonViewState.Type.CONTENT ->
                PokemonViewContentHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.pokemon_itemview, parent, false)
                )
            PokemonViewState.Type.LOADING ->
                PokemonViewLoadingHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.pokemon_loading_itemview, parent, false)
                )
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PokemonViewState.Content -> (holder as PokemonViewContentHolder).bind(
                item,
                onClickEvent
            )
            is PokemonViewState.Loading -> onLoadingEvent.invoke()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

}

object PokemonDiffUtil : DiffUtil.ItemCallback<PokemonViewState>() {
    override fun areItemsTheSame(oldItem: PokemonViewState, newItem: PokemonViewState) =
        oldItem is PokemonViewState.Loading && newItem is PokemonViewState.Loading
                || oldItem is PokemonViewState.Content && newItem is PokemonViewState.Content
                && oldItem.pokemonId == newItem.pokemonId

    override fun areContentsTheSame(oldItem: PokemonViewState, newItem: PokemonViewState) =
        oldItem == newItem
}

class PokemonViewContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val pokemonCard: CardView = itemView.findViewById(R.id.pokemon_card)
    private val pokemonNameTextView: TextView = itemView.findViewById(R.id.pokemon_name)
    private val pokemonImageView: ImageView = itemView.findViewById(R.id.pokemon_image)

    fun bind(state: PokemonViewState.Content, onClickEvent: (PokemonViewState.Content) -> Unit) {
        pokemonCard.setOnClickListener { onClickEvent(state) }
        pokemonNameTextView.text = state.pokemonName
        Glide.with(pokemonImageView).load(state.pokemonImageUrl)
            .fitCenter()
            .into(pokemonImageView)
    }

}

class PokemonViewLoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
