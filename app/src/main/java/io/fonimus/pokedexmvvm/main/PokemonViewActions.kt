package io.fonimus.pokedexmvvm.main

import android.view.View

sealed class PokemonViewActions {
    data class NavigateToDetail(
        val pokemonId: String,
        val pokemonName: String,
        val pokemonImageUrl: String,
        val textView: View,
        val imageView: View
    ) : PokemonViewActions()

    data class NavigateToDetail2(val pokemonId: String) : PokemonViewActions()
}
