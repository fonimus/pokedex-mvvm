package io.fonimus.pokedexmvvm.domain

import android.util.Log
import io.fonimus.pokedexmvvm.data.PokemonRepository
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class LoadPokemonUseCase @Inject constructor(private val repository: PokemonRepository) {

    private val pageStateFlow =
        MutableStateFlow(0).apply { onEach { Log.d("myusecase", "page stateflow called: $it") } }

    private val pokemons = mutableListOf<PokemonEntity>()

    operator fun invoke(): Flow<List<PokemonEntity>> {

        return pageStateFlow.flatMapConcat {
            repository.getPokemonPage(it)
        }.map { pokemonPage ->
            pokemons.addAll(pokemonPage.mapNotNull { pokemon ->
                PokemonEntity.Content(
                    pokemon.id?.toString() ?: return@mapNotNull null,
                    pokemon.name ?: return@mapNotNull null,
                    pokemon.sprites?.frontDefault ?: return@mapNotNull null,
                    pokemon.types.mapNotNull {
                        PokemonTypeEntity.parse(it.type?.name)
                    }
                )
            })
            pokemons + PokemonEntity.Loading(true)
        }
    }

    fun nextPage() {
        pageStateFlow.value++
        Log.d("mynextpage", "Next page called")
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
