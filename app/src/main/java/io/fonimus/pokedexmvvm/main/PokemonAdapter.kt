package io.fonimus.pokedexmvvm.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.fonimus.pokedexmvvm.R
import io.fonimus.pokedexmvvm.databinding.PokemonItemviewBinding

class PokemonAdapter(
    private val onClickEvent: (PokemonViewStateItem.Content, View, View) -> Unit,
    private val onLoadingEvent: () -> Unit,
    private val onFavoriteClicked: (String) -> Unit
) :
    ListAdapter<PokemonViewStateItem, RecyclerView.ViewHolder>(PokemonDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (PokemonViewStateItem.Type.values()[viewType]) {
            PokemonViewStateItem.Type.CONTENT ->
                PokemonViewContentHolder(
                    PokemonItemviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                onClickEvent,
                onFavoriteClicked
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

class PokemonViewContentHolder(private val pokemonItemViewBinding: PokemonItemviewBinding) : RecyclerView.ViewHolder(pokemonItemViewBinding.root) {

    fun bind(state: PokemonViewStateItem.Content, onClickEvent: (PokemonViewStateItem.Content, View, View) -> Unit, onFavoriteClicked: (String) -> Unit) {
        pokemonItemViewBinding.star.setImageResource(state.starResourceDrawable)
        pokemonItemViewBinding.pokemonCard.setOnClickListener { onClickEvent(state, pokemonItemViewBinding.pokemonName, pokemonItemViewBinding.pokemonImage) }
        pokemonItemViewBinding.star.setOnClickListener { onFavoriteClicked(state.pokemonId) }
        pokemonItemViewBinding.pokemonName.text = state.pokemonName
        pokemonItemViewBinding.pokemonName.transitionName = getTransitionName(state.pokemonId, TransitionType.TEXT)
        pokemonItemViewBinding.pokemonImage.transitionName = getTransitionName(state.pokemonId, TransitionType.IMAGE)
        Glide.with(pokemonItemViewBinding.pokemonImage).load(state.pokemonImageUrl)
            .fitCenter()
            .into(pokemonItemViewBinding.pokemonImage)
    }

}

class PokemonViewLoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

fun getTransitionName(pokemonId: String, type: TransitionType): String = "transition-$type-${pokemonId}"
enum class TransitionType {
    TEXT,IMAGE
}
