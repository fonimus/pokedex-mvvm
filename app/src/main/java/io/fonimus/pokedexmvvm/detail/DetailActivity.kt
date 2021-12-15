package io.fonimus.pokedexmvvm.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import io.fonimus.pokedexmvvm.R

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
        val pokemonDetailImageView = findViewById<ImageView>(R.id.pokemon_detail_image)
        val pokemonDetailTypeChip1 = findViewById<Chip>(R.id.pokemon_detail_type_1)
        val pokemonDetailTypeChip2 = findViewById<Chip>(R.id.pokemon_detail_type_2)
        val pokemonDetailNumberTextView = findViewById<TextView>(R.id.pokemon_detail_number_content)
        val pokemonDetailWeightTextView = findViewById<TextView>(R.id.pokemon_detail_weight_content)

        pokemonDetailViewModel.pokemonViewStateLiveData.observe(this) {
            pokemonDetailNameTextView.text = it.pokemonName
            pokemonDetailNumberTextView.text = it.pokemonNumber
            pokemonDetailWeightTextView.text = it.pokemonWeight
            pokemonDetailTypeChip1.text = it.type1.name
            pokemonDetailTypeChip1.setChipIcon(it.type1.icon)
            pokemonDetailTypeChip1.setChipBackgroundColorResource(it.type1.color)
            if(it.type2 != null) {
                pokemonDetailTypeChip2.text = it.type2.name
                pokemonDetailTypeChip2.setChipIcon(it.type2.icon)
                pokemonDetailTypeChip2.setChipBackgroundColorResource(it.type2.color)
            }
            pokemonDetailTypeChip2.isVisible = it.isType2Visible
            Glide.with(pokemonDetailImageView).load(it.pokemonImageUrl)
                .fitCenter()
                .into(pokemonDetailImageView)
        }
        pokemonDetailViewModel.init(intent.getStringExtra(POKEMON_ID)!!)
    }
}

fun Chip.setChipIcon(@DrawableRes res: Int?) {
    if (res != null) {
        chipIcon = AppCompatResources.getDrawable(context, res)
    }
}
