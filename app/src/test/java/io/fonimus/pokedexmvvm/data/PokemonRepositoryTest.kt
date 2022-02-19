package io.fonimus.pokedexmvvm.data

import app.cash.turbine.test
import io.fonimus.pokedexmvvm.data.pokemon.PokemonResponse
import io.fonimus.pokedexmvvm.domain.CoroutineDispatchers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PokemonRepositoryTest {
    val unconfinedTestDispatcher = UnconfinedTestDispatcher()
    val dispatchers = mockk<CoroutineDispatchers> {
        every { io } returns unconfinedTestDispatcher
        every { main } returns unconfinedTestDispatcher
    }

    val api = mockk<PokeApi>()
    val repo = PokemonRepository(api, dispatchers)

    @Test
    fun getPokemonPage() = runTest {
        repeat(5) {
            val id = it + 1
            coEvery { api.getPokemonById(id.toString()) } returns PokemonResponse(
                id = id, name = "name-$id"
            )
        }

        val result = repo.getPokemonPage(0).first()

        assertEquals(List(5) {
            val id = it + 1
            PokemonResponse(
                id = id, name = "name-$id"
            )
        }, result)
    }

    @Test
    fun getPokemonById() = runTest {
        val expected = mockk<PokemonResponse>()
        coEvery { api.getPokemonById(any()) } returns expected

        assertEquals(expected, repo.getPokemonById("27"))

        coVerify(exactly = 1) { api.getPokemonById("27") }
    }

    @Test
    fun refresh_turbine() = runTest(unconfinedTestDispatcher) {
        repeat(5) {
            val id = it + 1
            coEvery { api.getPokemonById(id.toString()) } coAnswers {
                delay(10)
                PokemonResponse(
                    id = id, name = "name-$id"
                )
            }
        }

        repo.getPokemonPage(0).test {

            println("Time::$currentTime")
            delay(37)
            println("Time::$currentTime")

            assertEquals(List(5) {
                val id = it + 1
                PokemonResponse(
                    id = id, name = "name-$id"
                )
            }, awaitItem())

            println("Time::$currentTime")

            repo.refresh()

            println("Time::$currentTime")

            assertEquals(List(5) {
                val id = it + 1
                PokemonResponse(
                    id = id, name = "name-$id"
                )
            }, awaitItem())

            println("Time::$currentTime")
        }
    }

    @Test
    fun refresh_normal() = runTest {
        repeat(10) {
            val id = it + 1
            coEvery { api.getPokemonById(id.toString()) } returns PokemonResponse(
                id = id, name = "name-$id"
            )
        }

        repo.getPokemonPage(1).first()

        coVerify(exactly = 1) {
            repeat(5) {
                val id = it + 6
                api.getPokemonById(id.toString())
            }
        }

        repo.refresh()
        repo.getPokemonPage(0).first()

        coVerify(exactly = 1) {
            repeat(5) {
                val id = it + 1
                api.getPokemonById(id.toString())
            }
        }
    }
}


