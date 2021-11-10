package io.fonimus.pokedexmvvm.data

import io.fonimus.pokedexmvvm.data.pokemon.PokemonResponse
import io.fonimus.pokedexmvvm.data.pokemonlist.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {
    @GET("pokemon/{pokemonId}/")
    suspend fun getPokemonById(@Path("pokemonId") pokemonId : String) : PokemonResponse?

    @GET("pokemon")
    suspend fun getAllPokemon() : PokemonListResponse?
}
