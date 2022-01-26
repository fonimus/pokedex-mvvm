package io.fonimus.pokedexmvvm.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.fonimus.pokedexmvvm.databinding.DetailActivityBinding

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    companion object {

        private const val POKEMON_ID = "POKEMON_ID"
        private const val POKEMON_IMAGE_URL = "POKEMON_IMAGE_URL"

        fun navigate(context: Context, pokemonId: String, pokemonImageUrl: String) =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(POKEMON_ID, pokemonId)
                putExtra(POKEMON_IMAGE_URL, pokemonImageUrl)
            }
    }

    private lateinit var binding: DetailActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Init view binding
        binding = DetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(POKEMON_ID)!!
        val url = intent.getStringExtra(POKEMON_IMAGE_URL)!!

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.detailFragmentContainer.id, DetailFragment.newInstance(
                        id, url
                    )
                ).commit()
        }
    }
}
