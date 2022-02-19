package io.fonimus.pokedexmvvm.main

import androidx.annotation.DrawableRes
import io.fonimus.pokedexmvvm.domain.PokemonTypeEntity

data class PokemonViewState(
    val items: List<PokemonViewStateItem>,
    val types: Set<PokemonTypeEntity>,
    val isRefreshing: Boolean
)

sealed class PokemonViewStateItem(val type: Type) {
    enum class Type {
        CONTENT, LOADING
    }

    data class Content(
        val pokemonId: String,
        val pokemonName: String,
        val pokemonImageUrl: String,
        @DrawableRes
        val starResourceDrawable: Int
    ) : PokemonViewStateItem(Type.CONTENT)

    object Loading : PokemonViewStateItem(Type.LOADING)
}
