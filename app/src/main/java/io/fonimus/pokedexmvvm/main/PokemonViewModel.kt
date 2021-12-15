package io.fonimus.pokedexmvvm.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.SingleLiveEvent
import io.fonimus.pokedexmvvm.domain.LoadPokemonUseCase
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val loadPokemonUseCase: LoadPokemonUseCase) :
    ViewModel() {

    val viewActions = SingleLiveEvent<PokemonViewActions>()

    fun onPokemonClicked(pokemonViewState: PokemonViewState.Content) {
        viewActions.value = PokemonViewActions.NavigateToDetail(pokemonViewState.pokemonNumber)
    }

    fun loadNextPage() {
        loadPokemonUseCase.nextPage()
    }

    val pokemonViewStateLiveData: LiveData<List<PokemonViewState>> = liveData {
        loadPokemonUseCase().collect { pokemons ->
            emit(pokemons.map { pokemon ->
                when (pokemon) {
                    is LoadPokemonUseCase.PokemonEntity.Loading -> PokemonViewState.Loading
                    is LoadPokemonUseCase.PokemonEntity.Content -> PokemonViewState.Content(
                        pokemon.pokemonNumber,
                        pokemon.pokemonNumber,
                        pokemon.pokemonImageUrl
                    )
                }
            })
        }
    }
}
