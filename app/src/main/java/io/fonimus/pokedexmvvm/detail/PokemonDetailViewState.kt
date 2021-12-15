package io.fonimus.pokedexmvvm.detail

import io.fonimus.pokedexmvvm.domain.PokemonTypeEntity

data class PokemonDetailViewState(
    val pokemonNumber: CharSequence,
    val pokemonName: CharSequence,
    val pokemonImageUrl: String,
    val pokemonWeight: CharSequence,
    val type1: PokemonTypeEntity,
    val type2: PokemonTypeEntity?,
    val isType2Visible: Boolean
)
