package io.fonimus.pokedexmvvm.data

import android.util.Log
import io.fonimus.pokedexmvvm.data.pokemon.PokemonResponse
import io.fonimus.pokedexmvvm.domain.CoroutineDispatchers
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val pokeApi: PokeApi,
    private val coroutineDispatchers: CoroutineDispatchers
    ) {

    private val triggerMutableStateFlow = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    fun getPokemonPage(page: Int = 0): Flow<List<PokemonResponse>> = triggerMutableStateFlow.flatMapLatest {

        flow {
            val list = mutableListOf<PokemonResponse>()
            val from = 1 + (page * 5)
            val to = 1 + ((page + 1) * 5)
            for (i in from until to) {
                pokeApi.getPokemonById(i.toString())?.let { list.add(it) }
                println("get pokemin $i done")
            }
            Log.d("myrepo", "getPokemonPage() called from $from to $to")
            emit(list)
        }
    }.flowOn(coroutineDispatchers.io)

    suspend fun getPokemonById(id: String) = pokeApi.getPokemonById(id)

    // useful when no pagination
    fun refresh() {
        triggerMutableStateFlow.tryEmit(Unit)
    }
}
