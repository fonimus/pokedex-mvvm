package io.fonimus.pokedexmvvm.detail

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class PokemonDetailViewState(
    val pokemonNumber: CharSequence,
    val pokemonName: CharSequence,
    val pokemonImageUrl: String,
    val pokemonWeight: CharSequence,
    val type1: PokemonDetailViewType,
    val type2: PokemonDetailViewType?,
    val isType2Visible: Boolean
)

data class PokemonDetailViewType(
    val name: CharSequence,
    @DrawableRes
    val icon: Int,
    @ColorRes
    val color: Int
)
