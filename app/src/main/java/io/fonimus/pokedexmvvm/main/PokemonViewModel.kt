package io.fonimus.pokedexmvvm.main

import android.view.View
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.SingleLiveEvent
import io.fonimus.pokedexmvvm.domain.LoadPokemonUseCase
import io.fonimus.pokedexmvvm.domain.PokemonTypeEntity
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val loadPokemonUseCase: LoadPokemonUseCase) :
    ViewModel() {

    val viewActions = SingleLiveEvent<PokemonViewActions>()

    private val pokemonEntitiesMediatorLiveData = MediatorLiveData<PokemonViewState>()

    val pokemonViewStateLiveData: LiveData<PokemonViewState> = pokemonEntitiesMediatorLiveData

    private val queryLiveData = MutableLiveData<String?>(null)

    private val typesLiveData = MutableLiveData<MutableList<PokemonTypeEntity>>(mutableListOf())

    private val pokemonEntitiesLiveData: LiveData<List<LoadPokemonUseCase.PokemonEntity>> =
        loadPokemonUseCase().asLiveData()

    init {
        pokemonEntitiesMediatorLiveData.addSource(queryLiveData) { query ->
            combine(query, typesLiveData.value!!, pokemonEntitiesLiveData.value)
        }
        pokemonEntitiesMediatorLiveData.addSource(typesLiveData) { types ->
            combine(queryLiveData.value, types, pokemonEntitiesLiveData.value)
        }
        pokemonEntitiesMediatorLiveData.addSource(pokemonEntitiesLiveData) { entities ->
            combine(queryLiveData.value, typesLiveData.value!!, entities)
        }
    }

    private fun combine(
        query: String?,
        types: List<PokemonTypeEntity>,
        entities: List<LoadPokemonUseCase.PokemonEntity>?
    ) {
        pokemonEntitiesMediatorLiveData.value = PokemonViewState(entities
            ?.filter {
                it is LoadPokemonUseCase.PokemonEntity.Loading
                        || (matchQuery(query, it) && matchTypes(types, it))
            }
            ?.map { pokemon ->
                when (pokemon) {
                    is LoadPokemonUseCase.PokemonEntity.Loading -> PokemonViewStateItem.Loading
                    is LoadPokemonUseCase.PokemonEntity.Content -> PokemonViewStateItem.Content(
                        pokemon.pokemonNumber,
                        pokemon.pokemonName,
                        pokemon.pokemonImageUrl
                    )
                }
            } ?: emptyList(), entities
            ?.asSequence()
            ?.filterIsInstance<LoadPokemonUseCase.PokemonEntity.Content>()
            ?.map { it.pokemonTypes }
            ?.flatten()
            ?.toSet()
            ?: emptySet()
        )
    }

    private fun matchQuery(query: String?, entity: LoadPokemonUseCase.PokemonEntity): Boolean {
        return query == null || (entity as? LoadPokemonUseCase.PokemonEntity.Content)?.pokemonName?.contains(
            query
        ) == true
    }

    private fun matchTypes(
        types: List<PokemonTypeEntity>,
        entity: LoadPokemonUseCase.PokemonEntity
    ): Boolean {
        return types.isEmpty() || (entity as? LoadPokemonUseCase.PokemonEntity.Content)?.pokemonTypes?.any {
            types.contains(
                it
            )
        } == true
    }

    fun onPokemonClicked(
        pokemonViewState: PokemonViewStateItem.Content,
        textView: View,
        imageView: View
    ) {
        viewActions.value = PokemonViewActions.NavigateToDetail(
            pokemonViewState.pokemonId,
            pokemonViewState.pokemonName,
            pokemonViewState.pokemonImageUrl,
            textView,
            imageView
        )
    }

    fun loadNextPage() {
        loadPokemonUseCase.nextPage()
    }

    fun onQueryTextChange(query: String?) {
        queryLiveData.value = query
    }

    fun onTypeChange(type: PokemonTypeEntity, checked: Boolean) {
        if (checked) {
            typesLiveData.value = typesLiveData.value?.apply { add(type) }
        } else {
            typesLiveData.value = typesLiveData.value?.apply { remove(type) }
        }
    }
}
