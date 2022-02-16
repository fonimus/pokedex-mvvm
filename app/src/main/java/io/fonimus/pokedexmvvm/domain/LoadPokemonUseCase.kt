package io.fonimus.pokedexmvvm.domain

import android.util.Log
import io.fonimus.pokedexmvvm.data.PokemonRepository
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadPokemonUseCase @Inject constructor(private val repository: PokemonRepository) {

    private val pageStateFlow =
        MutableSharedFlow<Int>(replay = 1).apply {
            tryEmit(0)
            onEach { Log.d("myusecase", "page stateflow called: $it") }
        }

    private val pokemons = mutableListOf<PokemonEntity>()

    operator fun invoke(): Flow<LoadPokemonResult> {

        return pageStateFlow.transformLatest {
            emit(LoadPokemonResult.Loading)
            repository.getPokemonPage(it).collect { pokemonPage ->
                pokemons.addAll(pokemonPage.mapNotNull { pokemon ->
                    PokemonEntity.Content(
                        pokemon.id?.toString() ?: return@mapNotNull null,
                        pokemon.name ?: return@mapNotNull null,
                        pokemon.sprites?.frontDefault ?: return@mapNotNull null,
                        pokemon.types.mapNotNull { pokemonType ->
                            PokemonTypeEntity.parse(pokemonType.type?.name)
                        }
                    )
                })
                emit(LoadPokemonResult.Content(pokemons + PokemonEntity.Loading(true)))
            }
        }
    }

    fun nextPage() {
        pageStateFlow.tryEmit(pageStateFlow.replayCache.first() + 1)
        Log.d("mynextpage", "Next page called")
    }

    fun refresh() {
        pageStateFlow.tryEmit(0)
    }

    sealed class PokemonEntity {
        data class Content(
            val pokemonNumber: String,
            val pokemonName: String,
            val pokemonImageUrl: String,
            val pokemonTypes: List<PokemonTypeEntity>
        ) : PokemonEntity()

        data class Loading(val loading: Boolean) : PokemonEntity()
    }
}

sealed class LoadPokemonResult {

    data class Content(
        val entities: List<LoadPokemonUseCase.PokemonEntity>
    ) : LoadPokemonResult()

    object Loading : LoadPokemonResult()
}
