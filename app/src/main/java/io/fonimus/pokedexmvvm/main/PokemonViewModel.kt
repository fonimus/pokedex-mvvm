package io.fonimus.pokedexmvvm.main

import android.view.View
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.R
import io.fonimus.pokedexmvvm.SingleLiveEvent
import io.fonimus.pokedexmvvm.combine
import io.fonimus.pokedexmvvm.data.CurrentSearchRepository
import io.fonimus.pokedexmvvm.data.FavoriteEntity
import io.fonimus.pokedexmvvm.data.FavoritesDao
import io.fonimus.pokedexmvvm.domain.CoroutineDispatchers
import io.fonimus.pokedexmvvm.domain.LoadPokemonResult
import io.fonimus.pokedexmvvm.domain.LoadPokemonUseCase
import io.fonimus.pokedexmvvm.domain.PokemonTypeEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val loadPokemonUseCase: LoadPokemonUseCase,
    currentSearchRepository: CurrentSearchRepository,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val favoritesDao: FavoritesDao
) :
    ViewModel() {

    val viewActions = SingleLiveEvent<PokemonViewActions>()

    private val queryLiveData =
        currentSearchRepository.searchQueryFlow.asLiveData(coroutineDispatchers.io)

    private val typesLiveData = MutableLiveData<MutableList<PokemonTypeEntity>>(mutableListOf())

    private val refreshingLiveData = MutableLiveData(false)

    private val favoritesLiveData = favoritesDao.getAll().asLiveData(coroutineDispatchers.io)

    private val pokemonEntitiesLiveData: LiveData<LoadPokemonResult> =
        loadPokemonUseCase().asLiveData(coroutineDispatchers.io)

    val pokemonViewStateLiveData: LiveData<PokemonViewState> = combine(
        queryLiveData,
        typesLiveData,
        pokemonEntitiesLiveData,
        refreshingLiveData,
        favoritesLiveData
    ) { query: String?, types: List<PokemonTypeEntity>?, result: LoadPokemonResult?, refreshing: Boolean?, favorites: List<FavoriteEntity>? ->

        if (refreshing!! && result is LoadPokemonResult.Content) {
            refreshingLiveData.value = false
            return@combine
        }
        emit(PokemonViewState((result as? LoadPokemonResult.Content)?.entities
            ?.asSequence()
            ?.filter {
                it is LoadPokemonUseCase.PokemonEntity.Loading
                        || (matchQuery(query, it) && matchTypes(types!!, it))
            }
            ?.map { pokemon ->
                when (pokemon) {
                    is LoadPokemonUseCase.PokemonEntity.Loading -> PokemonViewStateItem.Loading
                    is LoadPokemonUseCase.PokemonEntity.Content -> PokemonViewStateItem.Content(
                        pokemonId = pokemon.pokemonNumber,
                        pokemonName = pokemon.pokemonName,
                        pokemonImageUrl = pokemon.pokemonImageUrl,
                        starResourceDrawable = if (favorites?.find { it.pokemonId == pokemon.pokemonNumber } != null) {
                            R.drawable.ic_baseline_star_24
                        } else {
                            R.drawable.ic_baseline_star_outline_24
                        }
                    )
                }
            }?.toList() ?: emptyList(), (result as? LoadPokemonResult.Content)?.entities
            ?.asSequence()
            ?.filterIsInstance<LoadPokemonUseCase.PokemonEntity.Content>()
            ?.map { it.pokemonTypes }
            ?.flatten()
            ?.toSet()
            ?: emptySet(), refreshing && result is LoadPokemonResult.Loading
        ))
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

    fun onTypeChange(type: PokemonTypeEntity, checked: Boolean) {
        if (checked) {
            typesLiveData.value = typesLiveData.value?.apply { add(type) }
        } else {
            typesLiveData.value = typesLiveData.value?.apply { remove(type) }
        }
    }

    fun refresh() {
        loadPokemonUseCase.refresh()
        refreshingLiveData.value = true
    }

    fun onFavoriteClicked(id: String) {
        viewModelScope.launch(coroutineDispatchers.io) {
            if (favoritesDao.getById(id) == null) {
                favoritesDao.insert(
                    FavoriteEntity(
                        pokemonId = id
                    )
                )
            } else {
                favoritesDao.deleteById(id)
            }
        }
    }
}
