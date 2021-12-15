package io.fonimus.pokedexmvvm.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.SearchView
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

        val onLoadingEventListener: () -> Unit = {
//            pokemonViewModel.loadNextPage()
        }
        val onPokemonClicked: (PokemonViewState.Content) -> Unit = {
            pokemonViewModel.onPokemonClicked(it)
        }
        val recyclerView: RecyclerView = findViewById(R.id.main_recycler_view)
        val adapter = PokemonAdapter(onPokemonClicked, onLoadingEventListener)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(
                        layoutManager.findLastCompletelyVisibleItemPosition()
                    )
                    if (viewHolder is PokemonViewLoadingHolder) {
                        pokemonViewModel.loadNextPage()
                    }
                }
            }
        })

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val search = menu.findItem(R.id.search_pokemon).actionView as SearchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                // when search in keyboard clicked
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                pokemonViewModel.onQueryTextChange(query)
                return true
            }

        })
        return true
    }
}
