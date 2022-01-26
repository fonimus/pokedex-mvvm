package io.fonimus.pokedexmvvm.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import io.fonimus.pokedexmvvm.databinding.DetailFragmentBinding
import io.fonimus.pokedexmvvm.main.TransitionType
import io.fonimus.pokedexmvvm.main.getTransitionName

@AndroidEntryPoint
class DetailFragment : Fragment() {

    companion object {

        private const val POKEMON_ID = "POKEMON_ID"
        private const val POKEMON_IMAGE_URL = "POKEMON_IMAGE_URL"

        fun newInstance(pokemonId: String, pokemonImageUrl: String) = DetailFragment().apply {
            arguments = Bundle().apply {
                putString(POKEMON_ID, pokemonId)
                putString(POKEMON_IMAGE_URL, pokemonImageUrl)
            }
        }
    }

    private val pokemonDetailViewModel by viewModels<PokemonDetailViewModel>()

    private var _binding: DetailFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("destroy", "On destroy [detail]")
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pokemonId = requireArguments().getString(POKEMON_ID)!!
        val pokemonImageUrl = requireArguments().getString(POKEMON_IMAGE_URL)!!

        binding.pokemonDetailName.transitionName = getTransitionName(pokemonId, TransitionType.TEXT)
        binding.pokemonDetailImage.transitionName =
            getTransitionName(pokemonId, TransitionType.IMAGE)

        postponeEnterTransition()
        Glide.with(binding.pokemonDetailImage).load(pokemonImageUrl)
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
            .into(binding.pokemonDetailImage)

        pokemonDetailViewModel.pokemonViewStateLiveData.observe(viewLifecycleOwner) {
            binding.pokemonDetailName.text = it.pokemonName
            binding.pokemonDetailNumberContent.text = it.pokemonNumber
            binding.pokemonDetailWeightContent.text = it.pokemonWeight
            binding.pokemonDetailType1.text = it.type1.name
            binding.pokemonDetailType1.setChipIconResource(it.type1.icon)
            binding.pokemonDetailType1.setChipBackgroundColorResource(it.type1.color)
            if (it.type2 != null) {
                binding.pokemonDetailType2.text = it.type2.name
                binding.pokemonDetailType2.setChipIconResource(it.type2.icon)
                binding.pokemonDetailType2.setChipBackgroundColorResource(it.type2.color)
            }
            binding.pokemonDetailType2.isVisible = it.isType2Visible
            Glide.with(binding.pokemonDetailImage).load(it.pokemonImageUrl)
                .fitCenter()
                .into(binding.pokemonDetailImage)
        }
        pokemonDetailViewModel.init(pokemonId)
    }
}
