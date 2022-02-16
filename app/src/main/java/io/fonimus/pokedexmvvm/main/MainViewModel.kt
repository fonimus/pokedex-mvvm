package io.fonimus.pokedexmvvm.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.data.CurrentSearchRepository
import io.fonimus.pokedexmvvm.domain.LoadPokemonUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currentSearchRepository: CurrentSearchRepository,
    private val useCase: LoadPokemonUseCase
    ) :
    ViewModel() {

    fun onQueryTextChange(query: String?) {
        currentSearchRepository.onSearchQueryChange(query)
    }

    fun refresh() {
        useCase.refresh()
    }
}
