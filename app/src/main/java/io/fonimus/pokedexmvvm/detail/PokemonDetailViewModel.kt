package io.fonimus.pokedexmvvm.detail

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.data.PokemonRepository
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
                if (pokemon?.id != null && pokemon.name != null && pokemon.sprites?.frontDefault != null) {
                    emit(
                        PokemonDetailViewState(
                            pokemon.id.toString(),
                            pokemon.name,
                            pokemon.sprites.frontDefault
                        )
                    )
                }
            }
        }
}
