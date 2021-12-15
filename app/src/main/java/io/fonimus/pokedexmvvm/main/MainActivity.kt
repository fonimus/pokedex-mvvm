package io.fonimus.pokedexmvvm.main

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import io.fonimus.pokedexmvvm.R
import io.fonimus.pokedexmvvm.detail.DetailActivity
import io.fonimus.pokedexmvvm.domain.PokemonTypeEntity
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
        val onPokemonClicked: (PokemonViewStateItem.Content, View, View) -> Unit =
            { content, textView, imageView ->
                pokemonViewModel.onPokemonClicked(content, textView, imageView)
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

        val searchTypes = findViewById<ChipGroup>(R.id.search_types)
        pokemonViewModel.pokemonViewStateLiveData.observe(this) { state ->
            adapter.submitList(state.items)
            state.types.forEach { type -> searchTypes.addChip(type) }
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

    private fun ChipGroup.addChip(type: PokemonTypeEntity) {
        val chip = Chip(context)
        chip.text = type.name
        chip.isCheckable = true
        chip.setChipIconResource(type.icon)
        chip.setChipBackgroundColorResource(R.color.grey)
        chip.isCheckedIconVisible = false
        chip.setOnCheckedChangeListener { _, checked: Boolean ->
            pokemonViewModel.onTypeChange(type, checked)
            if (checked) {
                chip.setChipBackgroundColorResource(type.color)
            } else {
                chip.setChipBackgroundColorResource(R.color.grey)
            }
            chip.isCloseIconVisible = checked
        }
        addView(chip)
    }
}
