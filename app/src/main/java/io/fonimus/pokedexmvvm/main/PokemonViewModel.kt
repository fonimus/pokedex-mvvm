package io.fonimus.pokedexmvvm.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.SingleLiveEvent
import io.fonimus.pokedexmvvm.data.PokemonRepository
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val repository: PokemonRepository) :
    ViewModel() {

    val viewActions = SingleLiveEvent<PokemonViewActions>()

    fun onPokemonClicked(pokemonViewState: PokemonViewState) {
        viewActions.value = PokemonViewActions.NavigateToDetail(pokemonViewState.pokemonNumber)
    }

    val pokemonViewStateLiveData: LiveData<List<PokemonViewState>> = liveData {
        repository.getAllPokemons().collect {
            emit(it.mapNotNull { pokemon ->
                PokemonViewState(
                    pokemon.id?.toString() ?: return@mapNotNull null,
                    pokemon.name ?: return@mapNotNull null,
                    pokemon.sprites?.frontDefault ?: return@mapNotNull null
                )
            }
            )
        }
    }
}
