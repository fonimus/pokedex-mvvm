package io.fonimus.pokedexmvvm.domain

import app.cash.turbine.test
import io.fonimus.pokedexmvvm.data.PokemonRepository
import io.fonimus.pokedexmvvm.data.pokemon.PokemonResponse
import io.fonimus.pokedexmvvm.data.pokemon.PokemonSprites
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LoadPokemonUseCaseTest {
    val unconfinedTestDispatcher = UnconfinedTestDispatcher()
    val dispatchers = mockk<CoroutineDispatchers> {
        every { io } returns unconfinedTestDispatcher
        every { main } returns unconfinedTestDispatcher
    }

    val repository = mockk<PokemonRepository>()
    val useCase = LoadPokemonUseCase(repository, dispatchers)

    @Test
    operator fun invoke() = runTest(unconfinedTestDispatcher) {
        val p = PokemonResponse(
            id = 1, name = "name-1", types = listOf(),
            sprites = PokemonSprites(frontDefault = "url-1")
        )
        every { repository.getPokemonPage(any()) } returns flowOf(listOf(p))

        useCase.invoke().test {
            assertEquals(LoadPokemonResult.Loading, awaitItem())
            assertEquals(
                LoadPokemonResult.Content(
                    listOf(
                        LoadPokemonUseCase.PokemonEntity.Content("1", "name-1", "url-1", listOf())
                    ) + LoadPokemonUseCase.PokemonEntity.Loading(true)
                ), awaitItem()
            )
        }

    }
}
