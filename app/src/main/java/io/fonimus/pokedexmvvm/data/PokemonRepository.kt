package io.fonimus.pokedexmvvm.data

import android.util.Log
import io.fonimus.pokedexmvvm.data.pokemon.PokemonResponse
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor() {

    private var pokeApi: PokeApi
    private val triggerMutableStateFlow = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    )
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        pokeApi = retrofit.create(PokeApi::class.java)
    }

    fun getPokemonPage(page: Int = 0): Flow<List<PokemonResponse>> = triggerMutableStateFlow.flatMapLatest {

        flow {
            val list = mutableListOf<PokemonResponse>()
            val from = 1 + (page * 5)
            val to = 1 + ((page + 1) * 5)
            for (i in from until to) {
                pokeApi.getPokemonById(i.toString())?.let { list.add(it) }
            }
            Log.d("myrepo", "getPokemonPage() called from $from to $to")
            emit(list)
        }
    }

    suspend fun getPokemonById(id: String) = pokeApi.getPokemonById(id)

    // useful when no pagination
    fun refresh() {
        triggerMutableStateFlow.tryEmit(Unit)
    }
}
