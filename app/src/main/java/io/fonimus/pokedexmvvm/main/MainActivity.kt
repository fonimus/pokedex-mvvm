package io.fonimus.pokedexmvvm.main

import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.fonimus.pokedexmvvm.R
import io.fonimus.pokedexmvvm.SettingsActivity
import io.fonimus.pokedexmvvm.detail.DetailActivity
import io.fonimus.pokedexmvvm.exhaustive

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val pokemonViewModel by viewModels<PokemonViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val typeRecyclerView: RecyclerView = findViewById(R.id.main_search_types)
        val typeAdapter = TypeAdapter { type, checked ->
            pokemonViewModel.onTypeChange(type, checked)
        }
        typeRecyclerView.adapter = typeAdapter
        typeRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val pokemonsRecyclerView: RecyclerView = findViewById(R.id.main_recycler_view)
        val pokemonsAdapter = PokemonAdapter(
            { content, textView, imageView ->
                pokemonViewModel.onPokemonClicked(content, textView, imageView)
            }, {
                // nothing yet
            }
        )
        pokemonsRecyclerView.adapter = pokemonsAdapter
        val layoutManager = LinearLayoutManager(this)
        pokemonsRecyclerView.layoutManager = layoutManager
        pokemonsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        pokemonViewModel.pokemonViewStateLiveData.observe(this) { state ->
            pokemonsAdapter.submitList(state.items)
            typeAdapter.submitList(state.types.toList())
        }
        pokemonViewModel.viewActions.observe(this) {
            when (it) {
                is PokemonViewActions.NavigateToDetail -> {
                    val imageViewPair = Pair(it.imageView, it.imageView.transitionName)
                    val textViewPair = Pair(it.textView, it.textView.transitionName)

                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this,
                        imageViewPair,
                        textViewPair
                    )
                    startActivity(
                        DetailActivity.navigate(
                            this,
                            it.pokemonId,
                            it.pokemonId
                        ), options.toBundle()
                    )
                }
                is PokemonViewActions.NavigateToDetail2 -> TODO()
            }.exhaustive
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        (menu.findItem(R.id.search_pokemon).actionView as SearchView).setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                // when search in keyboard clicked
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                pokemonViewModel.onQueryTextChange(query)
                return true
            }
        })

        menu.findItem(R.id.open_settings).setOnMenuItemClickListener {
            startActivity(SettingsActivity.navigate(this))
            true
        }
        return true
    }
}
