package io.fonimus.pokedexmvvm.main

import io.fonimus.pokedexmvvm.CoroutineTest
import io.fonimus.pokedexmvvm.data.CurrentSearchRepository
import io.fonimus.pokedexmvvm.domain.CoroutineDispatchers
import io.fonimus.pokedexmvvm.domain.LoadPokemonResult
import io.fonimus.pokedexmvvm.domain.LoadPokemonUseCase
import io.fonimus.pokedexmvvm.domain.PokemonTypeEntity
import io.fonimus.pokedexmvvm.observeForTesting
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.lang.IllegalArgumentException

internal class PokemonViewModelTest : CoroutineTest {

    override lateinit var testScope: CoroutineScope
    override lateinit var dispatcher: CoroutineDispatcher
    override lateinit var dispatchers: CoroutineDispatchers

    val useCase = mockk<LoadPokemonUseCase>()
    val searchRepo = mockk<CurrentSearchRepository>()

    @Test
    fun getViewActions() {
    }

    @Test
    fun `GetPokemonViewStateLiveData Nominal`() = runTest(dispatcher) {
        every { searchRepo.searchQueryFlow } returns flowOf(null)
        every { useCase() } returns flowOf(
            LoadPokemonResult.Content(
                List(5) { getDefaultPokemonEntityContent(it) }
            )
        )

        val viewModel = PokemonViewModel(useCase, searchRepo, dispatchers)

        viewModel.pokemonViewStateLiveData.observeForTesting {
            assertEquals(
                PokemonViewState(
                    items = List(5) { id -> getDefaultPokemonViewStateContent(id) },
                    types = setOf(PokemonTypeEntity.DARK),
                    isRefreshing = false
                ), it.value
            )
        }
    }

    @ParameterizedTest
    @EnumSource(value = PokemonTypeEntity::class)
    fun `GetPokemonViewStateLiveData Search`(type: PokemonTypeEntity) = runTest(dispatcher) {
        every { searchRepo.searchQueryFlow } returns flowOf("2")
        every { useCase() } returns flowOf(
            LoadPokemonResult.Content(
                List(5) { getDefaultPokemonEntityContent(it).copy(pokemonTypes = listOf(type)) }
            )
        )

        val viewModel = PokemonViewModel(useCase, searchRepo, dispatchers)

        viewModel.pokemonViewStateLiveData.observeForTesting {
            assertEquals(
                PokemonViewState(
                    items = listOf(getDefaultPokemonViewStateContent("2".toInt())),
                    types = setOf(type),
                    isRefreshing = false
                ), it.value
            )
        }
    }

    @Test
    fun `getPokemonViewStateLiveData Loading`() = runTest(dispatcher) {
        every { searchRepo.searchQueryFlow } returns flowOf(null)
        every { useCase() } returns flowOf(LoadPokemonResult.Loading)

        val viewModel = PokemonViewModel(useCase, searchRepo, dispatchers)

        viewModel.pokemonViewStateLiveData.observeForTesting {
            assertEquals(PokemonViewState(listOf(), setOf(), false), it.value)
        }
    }

    @Test
    fun onPokemonClicked() {
    }

    @Test
    fun loadNextPage() {
    }

    @Test
    fun onTypeChange() {
    }

    @Test
    fun refresh() {
    }

    companion object {
        private const val EXPECTED_NUMBER = "1"
        private const val EXPECTED_NAME_PREFIX = "name-"
        private const val EXPECTED_URL_PREFIX = "url-"
        private val EXPECTED_TYPE = PokemonTypeEntity.DARK
    }

    private fun getDefaultPokemonEntityContent(index: Int) =
        LoadPokemonUseCase.PokemonEntity.Content(
            pokemonNumber = "$EXPECTED_NUMBER$index",
            pokemonName = "$EXPECTED_NAME_PREFIX$index",
            pokemonImageUrl = "$EXPECTED_URL_PREFIX$index",
            pokemonTypes = listOf(EXPECTED_TYPE)
        )

    private fun getDefaultPokemonViewStateContent(index: Int) = PokemonViewStateItem.Content(
        pokemonId = "$EXPECTED_NUMBER$index",
        pokemonName = "$EXPECTED_NAME_PREFIX$index",
        pokemonImageUrl = "$EXPECTED_URL_PREFIX$index"
    )


}
