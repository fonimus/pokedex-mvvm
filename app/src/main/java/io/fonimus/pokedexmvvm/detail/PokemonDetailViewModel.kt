package io.fonimus.pokedexmvvm.detail

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.data.PokemonRepository
import io.fonimus.pokedexmvvm.data.pokemon.PokemonTypeItem
import io.fonimus.pokedexmvvm.domain.PokemonTypeEntity
import java.util.*
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
                            type1,
                            type2,
                            type2 != null
                        )
                    )
                }
            }
        }

    private fun getType(types: List<PokemonTypeItem?>?, typeNumber: Int): PokemonTypeEntity? {
        val name = types?.firstOrNull {
            it?.slot == typeNumber
        }?.type?.name
        if (name != null) {
            return PokemonTypeEntity.valueOf(name.toUpperCase(Locale.getDefault()))
        }
        return null
    }
}
