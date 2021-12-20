package io.fonimus.pokedexmvvm

import io.fonimus.pokedexmvvm.domain.PokemonTypeEntity

val <T> T.exhaustive: T
    get() = this

fun catchOrDefault(default: PokemonTypeEntity, block: () -> PokemonTypeEntity) =
    try {
        block()
    } catch (e: Throwable) {
        default
    }
