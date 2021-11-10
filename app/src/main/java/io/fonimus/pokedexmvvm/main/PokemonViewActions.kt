package io.fonimus.pokedexmvvm.main

sealed class PokemonViewActions {
    data class NavigateToDetail(val pokemonId: String) : PokemonViewActions()
    data class NavigateToDetail2(val pokemonId: String) : PokemonViewActions()
}
