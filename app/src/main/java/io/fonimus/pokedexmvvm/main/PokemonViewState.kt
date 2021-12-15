package io.fonimus.pokedexmvvm.main

import io.fonimus.pokedexmvvm.domain.PokemonTypeEntity

data class PokemonViewState(
    val items: List<PokemonViewStateItem>,
    val types: Set<PokemonTypeEntity>
)

sealed class PokemonViewStateItem(val type: Type) {
    enum class Type {
        CONTENT, LOADING
    }

    data class Content(
        val pokemonId: String,
        val pokemonName: String,
        val pokemonImageUrl: String
    ) : PokemonViewStateItem(Type.CONTENT)

    object Loading : PokemonViewStateItem(Type.LOADING)
}
