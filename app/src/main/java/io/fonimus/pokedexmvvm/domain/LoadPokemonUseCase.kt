package io.fonimus.pokedexmvvm.domain

import android.util.Log
import io.fonimus.pokedexmvvm.data.PokemonRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class LoadPokemonUseCase @Inject constructor(private val repository: PokemonRepository) {

    //    private val isLoadingStateFlow = MutableStateFlow(true)
    private val pageStateFlow =
        MutableStateFlow(0).apply { onEach { Log.d("myusecase", "page stateflow called: $it") } }

    private val pokemons = mutableListOf<PokemonEntity>()

    operator fun invoke(): Flow<List<PokemonEntity>> {

//       return combine(
//            isLoadingStateFlow,
        return pageStateFlow.flatMapConcat {
            repository.getPokemonPage(it)
//                    .onCompletion { isLoadingStateFlow.value = false }
        }.map { pokemonPage ->
//        ) { isLoading, pokemonPage ->
            pokemons.addAll(pokemonPage.mapNotNull { pokemon ->
                PokemonEntity.Content(
                    pokemon.id?.toString() ?: return@mapNotNull null,
                    pokemon.name ?: return@mapNotNull null,
                    pokemon.sprites?.frontDefault ?: return@mapNotNull null
                )
            })
            pokemons + PokemonEntity.Loading(true)
        }
    }

    fun nextPage() {
//        isLoadingStateFlow.value = true
        pageStateFlow.value++
        Log.d("mynextpage", "Next page called")
    }

    sealed class PokemonEntity {
        data class Content(
            val pokemonNumber: String,
            val pokemonName: String,
            val pokemonImageUrl: String
        ) : PokemonEntity()

        data class Loading(val loading: Boolean) : PokemonEntity()
    }
}
