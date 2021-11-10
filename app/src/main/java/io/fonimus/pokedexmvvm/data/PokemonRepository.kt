package io.fonimus.pokedexmvvm.data

import io.fonimus.pokedexmvvm.data.pokemon.PokemonResponse
import io.fonimus.pokedexmvvm.data.pokemon.PokemonSprites
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor() {

    private var pokeApi: PokeApi

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

    fun getAllPokemons(): Flow<List<PokemonResponse>> = flow {
        val list = mutableListOf<PokemonResponse>()
        var i = 1
        while (true) {
            delay(500)
            if (i == 4) {
                list.add(
                    1,
                    PokemonResponse(
                        name = "TEST",
                        id = 2,
                        sprites = PokemonSprites(frontDefault = "https://www.pokepedia.fr/images/5/54/Sprite_MissingNo._RV.png")
                    )
                )
            }
            pokeApi.getPokemonById("${i++}")?.let { list.add(it) }
            emit(list)
        }
    }

    suspend fun getPokemonById(id: String) = pokeApi.getPokemonById(id)
}
