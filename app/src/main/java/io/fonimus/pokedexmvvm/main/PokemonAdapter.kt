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
    private val onClickEvent: (PokemonViewStateItem.Content, View, View) -> Unit,
    private val onLoadingEvent: () -> Unit
) :
    ListAdapter<PokemonViewStateItem, RecyclerView.ViewHolder>(PokemonDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (PokemonViewStateItem.Type.values()[viewType]) {
            PokemonViewStateItem.Type.CONTENT ->
                PokemonViewContentHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.pokemon_itemview, parent, false)
                )
            PokemonViewStateItem.Type.LOADING ->
                PokemonViewLoadingHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.pokemon_loading_itemview, parent, false)
                )
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PokemonViewStateItem.Content -> (holder as PokemonViewContentHolder).bind(
                item,
                onClickEvent
            )
            is PokemonViewStateItem.Loading -> onLoadingEvent.invoke()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

}

object PokemonDiffUtil : DiffUtil.ItemCallback<PokemonViewStateItem>() {
    override fun areItemsTheSame(oldItem: PokemonViewStateItem, newItem: PokemonViewStateItem) =
        oldItem is PokemonViewStateItem.Loading && newItem is PokemonViewStateItem.Loading
                || oldItem is PokemonViewStateItem.Content && newItem is PokemonViewStateItem.Content
                && oldItem.pokemonId == newItem.pokemonId

    override fun areContentsTheSame(oldItem: PokemonViewStateItem, newItem: PokemonViewStateItem) =
        oldItem == newItem
}

class PokemonViewContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val pokemonCard: CardView = itemView.findViewById(R.id.pokemon_card)
    private val pokemonNameTextView: TextView = itemView.findViewById(R.id.pokemon_name)
    private val pokemonImageView: ImageView = itemView.findViewById(R.id.pokemon_image)

    fun bind(state: PokemonViewStateItem.Content, onClickEvent: (PokemonViewStateItem.Content, View, View) -> Unit) {
        pokemonCard.setOnClickListener { onClickEvent(state, pokemonNameTextView, pokemonImageView) }
        pokemonNameTextView.text = state.pokemonName
        pokemonNameTextView.transitionName = getTransitionName(state.pokemonId, TransitionType.TEXT)
        pokemonImageView.transitionName = getTransitionName(state.pokemonId, TransitionType.IMAGE)
        Glide.with(pokemonImageView).load(state.pokemonImageUrl)
            .fitCenter()
            .into(pokemonImageView)
    }

}

class PokemonViewLoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

fun getTransitionName(pokemonId: String, type: TransitionType): String = "transition-$type-${pokemonId}"
enum class TransitionType {
    TEXT,IMAGE
}
