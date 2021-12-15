package io.fonimus.pokedexmvvm.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.SingleLiveEvent
import io.fonimus.pokedexmvvm.domain.LoadPokemonUseCase
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val loadPokemonUseCase: LoadPokemonUseCase) :
    ViewModel() {

    val viewActions = SingleLiveEvent<PokemonViewActions>()

    private val pokemonEntitiesMediatorLiveData = MediatorLiveData<List<PokemonViewState>>()

    private val queryLiveData = MutableLiveData<String?>(null)

    private val pokemonEntitiesLiveData: LiveData<List<LoadPokemonUseCase.PokemonEntity>> = loadPokemonUseCase().asLiveData()

    init {
        pokemonEntitiesMediatorLiveData.addSource(queryLiveData) { query ->
            combine(query, pokemonEntitiesLiveData.value)
        }
        pokemonEntitiesMediatorLiveData.addSource(pokemonEntitiesLiveData) { entities ->
            combine(queryLiveData.value, entities)
        }
    }

    private fun combine(query: String?, entities: List<LoadPokemonUseCase.PokemonEntity>?) {
        pokemonEntitiesMediatorLiveData.value = entities
            ?.filter {
                it is LoadPokemonUseCase.PokemonEntity.Loading
                    || query == null
                    || (it as? LoadPokemonUseCase.PokemonEntity.Content)?.pokemonName?.contains(query) == true
            }
            ?.map { pokemon ->
                when (pokemon) {
                    is LoadPokemonUseCase.PokemonEntity.Loading -> PokemonViewState.Loading
                    is LoadPokemonUseCase.PokemonEntity.Content -> PokemonViewState.Content(
                        pokemon.pokemonNumber,
                        pokemon.pokemonName,
                        pokemon.pokemonImageUrl
                    )
                }
            }
    }

    fun onPokemonClicked(pokemonViewState: PokemonViewState.Content) {
        viewActions.value = PokemonViewActions.NavigateToDetail(pokemonViewState.pokemonId)
    }

    fun loadNextPage() {
        loadPokemonUseCase.nextPage()
    }

    fun onQueryTextChange(query: String?) {
        queryLiveData.value = query
    }
}
