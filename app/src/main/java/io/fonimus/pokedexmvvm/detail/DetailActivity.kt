package io.fonimus.pokedexmvvm.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.fonimus.pokedexmvvm.R
import io.fonimus.pokedexmvvm.main.PokemonViewModel

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    companion object {

        private const val POKEMON_ID = "POKEMON_ID"

        fun navigate(context: Context, pokemonId: String) =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(POKEMON_ID, pokemonId)
            }
    }

    private val pokemonDetailViewModel by viewModels<PokemonDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        val pokemonDetailNameTextView = findViewById<TextView>(R.id.pokemon_detail_name)

        pokemonDetailViewModel.pokemonViewStateLiveData.observe(this) {
            pokemonDetailNameTextView.text = it.pokemonName
        }
        pokemonDetailViewModel.init(intent.getStringExtra(POKEMON_ID)!!)
    }
}
