package io.fonimus.pokedexmvvm.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.fonimus.pokedexmvvm.R
import io.fonimus.pokedexmvvm.detail.DetailActivity
import io.fonimus.pokedexmvvm.exhaustive

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val pokemonViewModel by viewModels<PokemonViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.main_recycler_view)
        val adapter = PokemonAdapter {
            pokemonViewModel.onPokemonClicked(it)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        pokemonViewModel.pokemonViewStateLiveData.observe(this) { list ->
            adapter.submitList(list)
        }
        pokemonViewModel.viewActions.observe(this) {
            when (it) {
                is PokemonViewActions.NavigateToDetail -> startActivity(
                    DetailActivity.navigate(
                        this,
                        it.pokemonId
                    )
                )
                is PokemonViewActions.NavigateToDetail2 -> TODO()
            }.exhaustive
        }
    }
}
