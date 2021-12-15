package io.fonimus.pokedexmvvm.detail

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import io.fonimus.pokedexmvvm.R
import io.fonimus.pokedexmvvm.main.TransitionType
import io.fonimus.pokedexmvvm.main.getTransitionName

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

    private val pokemonDetailViewModel by viewModels<PokemonDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        val pokemonId = intent.getStringExtra(POKEMON_ID)!!
        val pokemonImageUrl = intent.getStringExtra(POKEMON_IMAGE_URL)!!

        val pokemonDetailNameTextView = findViewById<TextView>(R.id.pokemon_detail_name)
        pokemonDetailNameTextView.transitionName = getTransitionName(pokemonId, TransitionType.TEXT)
        val pokemonDetailImageView = findViewById<ImageView>(R.id.pokemon_detail_image)
        pokemonDetailImageView.transitionName = getTransitionName(pokemonId, TransitionType.IMAGE)
        val pokemonDetailTypeChip1 = findViewById<Chip>(R.id.pokemon_detail_type_1)
        val pokemonDetailTypeChip2 = findViewById<Chip>(R.id.pokemon_detail_type_2)
        val pokemonDetailNumberTextView = findViewById<TextView>(R.id.pokemon_detail_number_content)
        val pokemonDetailWeightTextView = findViewById<TextView>(R.id.pokemon_detail_weight_content)

        postponeEnterTransition()
        Glide.with(pokemonDetailImageView).load(pokemonImageUrl)
            .fitCenter()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return true
                }

            })
            .into(pokemonDetailImageView)

        pokemonDetailViewModel.pokemonViewStateLiveData.observe(this) {
            pokemonDetailNameTextView.text = it.pokemonName
            pokemonDetailNumberTextView.text = it.pokemonNumber
            pokemonDetailWeightTextView.text = it.pokemonWeight
            pokemonDetailTypeChip1.text = it.type1.name
            pokemonDetailTypeChip1.setChipIconResource(it.type1.icon)
            pokemonDetailTypeChip1.setChipBackgroundColorResource(it.type1.color)
            if (it.type2 != null) {
                pokemonDetailTypeChip2.text = it.type2.name
                pokemonDetailTypeChip2.setChipIconResource(it.type2.icon)
                pokemonDetailTypeChip2.setChipBackgroundColorResource(it.type2.color)
            }
            pokemonDetailTypeChip2.isVisible = it.isType2Visible
            Glide.with(pokemonDetailImageView).load(it.pokemonImageUrl)
                .fitCenter()
                .into(pokemonDetailImageView)
        }
        pokemonDetailViewModel.init(pokemonId)
    }
}
