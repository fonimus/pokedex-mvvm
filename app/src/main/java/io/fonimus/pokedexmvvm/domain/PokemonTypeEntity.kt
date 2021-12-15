package io.fonimus.pokedexmvvm.domain

import io.fonimus.pokedexmvvm.R

enum class PokemonTypeEntity(val color: Int, val icon: Int) {
    NORMAL(R.color.type_normal, R.drawable.ic_baseline_not_interested_24),
    FIRE(R.color.type_fire, R.drawable.ic_baseline_local_fire_department_24),
    WATER(R.color.type_water, R.drawable.ic_baseline_not_interested_24),
    ELECTRIC(R.color.type_electric, R.drawable.ic_baseline_not_interested_24),
    GRASS(R.color.type_grass, R.drawable.ic_baseline_not_interested_24),
    ICE(R.color.type_ice, R.drawable.ic_baseline_not_interested_24),
    FIGHTING(R.color.type_fighting, R.drawable.ic_baseline_not_interested_24),
    POISON(R.color.type_poison, R.drawable.ic_baseline_not_interested_24),
    GROUND(R.color.type_ground, R.drawable.ic_baseline_not_interested_24),
    FLYING(R.color.type_flying, R.drawable.ic_baseline_not_interested_24),
    PSYCHIC(R.color.type_psychic, R.drawable.ic_baseline_not_interested_24),
    BUG(R.color.type_bug, R.drawable.ic_baseline_not_interested_24),
    ROCK(R.color.type_rock, R.drawable.ic_baseline_not_interested_24),
    GHOST(R.color.type_ghost, R.drawable.ic_baseline_not_interested_24),
    DRAGON(R.color.type_dragon, R.drawable.ic_baseline_not_interested_24),
    DARK(R.color.type_dark, R.drawable.ic_baseline_not_interested_24),
    STEEL(R.color.type_steel, R.drawable.ic_baseline_not_interested_24),
    FAIRY(R.color.type_fairy, R.drawable.ic_baseline_not_interested_24);
}
