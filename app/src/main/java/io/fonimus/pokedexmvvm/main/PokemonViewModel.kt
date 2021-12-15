package io.fonimus.pokedexmvvm.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.SingleLiveEvent
import io.fonimus.pokedexmvvm.domain.LoadPokemonUseCase
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val loadPokemonUseCase: LoadPokemonUseCase) :
    ViewModel() {

    val viewActions = SingleLiveEvent<PokemonViewActions>()
    private val queryLiveData = MutableLiveData<String>()

    fun onPokemonClicked(pokemonViewState: PokemonViewState.Content) {
        viewActions.value = PokemonViewActions.NavigateToDetail(pokemonViewState.pokemonId)
    }

    fun loadNextPage() {
        loadPokemonUseCase.nextPage()
    }

    fun onQueryTextChange(query: String?) {
        query?.let {
            queryLiveData.value = it
        }
    }

    val pokemonViewStateLiveData: LiveData<List<PokemonViewState>> = liveData {
        loadPokemonUseCase().collect { pokemons ->
            emit(pokemons.map { pokemon ->
                when (pokemon) {
                    is LoadPokemonUseCase.PokemonEntity.Loading -> PokemonViewState.Loading
                    is LoadPokemonUseCase.PokemonEntity.Content -> PokemonViewState.Content(
                        pokemon.pokemonNumber,
                        pokemon.pokemonName,
                        pokemon.pokemonImageUrl
                    )
                }
            })
        }
    }
}
