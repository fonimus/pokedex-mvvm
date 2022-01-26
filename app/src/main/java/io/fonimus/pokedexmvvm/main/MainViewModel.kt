package io.fonimus.pokedexmvvm.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fonimus.pokedexmvvm.data.CurrentSearchRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val currentSearchRepository: CurrentSearchRepository) :
    ViewModel() {

    fun onQueryTextChange(query: String?) {
        currentSearchRepository.onSearchQueryChange(query)
    }
}
