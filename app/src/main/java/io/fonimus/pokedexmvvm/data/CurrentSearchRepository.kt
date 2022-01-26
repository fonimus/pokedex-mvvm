package io.fonimus.pokedexmvvm.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentSearchRepository @Inject constructor() {

    private val searchQueryMutableStateFlow = MutableStateFlow<String?>(null)
    val searchQueryFlow = searchQueryMutableStateFlow.asStateFlow()

    fun onSearchQueryChange(searchQuery: String?) {
        searchQueryMutableStateFlow.value = searchQuery
    }

}
