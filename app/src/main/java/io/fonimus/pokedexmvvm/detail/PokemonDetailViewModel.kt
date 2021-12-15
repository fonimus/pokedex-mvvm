package io.fonimus.pokedexmvvm.detail

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.R
import io.fonimus.pokedexmvvm.data.PokemonRepository
import io.fonimus.pokedexmvvm.data.pokemon.PokemonTypeItem
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(private val repository: PokemonRepository) :
    ViewModel() {

    private val pokemonId = MutableLiveData<String>()

    fun init(id: String) {
        pokemonId.value = id
    }

    val pokemonViewStateLiveData: LiveData<PokemonDetailViewState> =
        pokemonId.switchMap { id ->
            liveData {
                val pokemon = repository.getPokemonById(id)
                val type1 = getType(pokemon?.types, 1)
                if (pokemon?.id != null
                    && pokemon.name != null
                    && pokemon.sprites?.frontDefault != null
                    && pokemon.weight != null
                    && type1 != null
                ) {
                    val type2 = getType(pokemon.types, 2)
                    emit(
                        PokemonDetailViewState(
                            pokemon.id.toString(),
                            pokemon.name,
                            pokemon.sprites.frontDefault,
                            pokemon.weight.toString(),
                            PokemonDetailViewType(
                                type1,
                                getTypeIcon(type1),
                                getTypeColorRes(type1)
                            ),
                            if (type2 != null) PokemonDetailViewType(
                                type2,
                                getTypeIcon(type2),
                                getTypeColorRes(type2)
                            ) else null,
                            type2 != null
                        )
                    )
                }
            }
        }

    @DrawableRes
    private fun getTypeIcon(type: CharSequence): Int {
        return when (type) {
            "fire" -> R.drawable.ic_baseline_local_fire_department_24
            else -> R.drawable.ic_baseline_not_interested_24
        }
    }

    @ColorRes
    fun getTypeColorRes(type: CharSequence): Int = when (type) {
        "normal" -> R.color.type_normal
        "fire" -> R.color.type_fire
        "water" -> R.color.type_water
        "electric" -> R.color.type_electric
        "grass" -> R.color.type_grass
        "ice" -> R.color.type_ice
        "fighting" -> R.color.type_fighting
        "poison" -> R.color.type_poison
        "ground" -> R.color.type_ground
        "flying" -> R.color.type_flying
        "psychic" -> R.color.type_psychic
        "bug" -> R.color.type_bug
        "rock" -> R.color.type_rock
        "ghost" -> R.color.type_ghost
        "dragon" -> R.color.type_dragon
        "dark" -> R.color.type_dark
        "steel" -> R.color.type_steel
        "fairy" -> R.color.type_fairy
        else -> R.color.black
    }

    private fun getType(types: List<PokemonTypeItem?>?, typeNumber: Int): CharSequence? =
        types?.firstOrNull {
            it?.slot == typeNumber
        }?.type?.name
}
