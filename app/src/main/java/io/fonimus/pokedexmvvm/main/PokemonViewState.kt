package io.fonimus.pokedexmvvm.main

sealed class PokemonViewState(val type: Type) {
    enum class Type {
        CONTENT, LOADING
    }

    data class Content(
        val pokemonNumber: String,
        val pokemonName: String,
        val pokemonImageUrl: String
    ) : PokemonViewState(Type.CONTENT)

    object Loading : PokemonViewState(Type.LOADING)
}
