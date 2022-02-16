package io.fonimus.pokedexmvvm.main

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import io.fonimus.pokedexmvvm.databinding.MainFragmentBinding
import io.fonimus.pokedexmvvm.detail.DetailActivity
import io.fonimus.pokedexmvvm.exhaustive
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment().apply {
            arguments = Bundle().apply { }
        }
    }

    private val pokemonViewModel by viewModels<PokemonViewModel>()
    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            Log.d("prefs", "$key changed to ${sharedPreferences.getBoolean(key, false)}")
        }


    private var _binding: MainFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("destroy", "On destroy [main]")
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firebase.crashlytics.log("${javaClass.simpleName} : on view created")

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        Log.d("prefs", ">> " + prefs.getString("signature", "N/A"))
        val editor = prefs.edit()
        editor.putString("toto", "titi")
        // apply -> value changed in memory
        // commit -> force waiting to write into file
        editor.apply()

        prefs.registerOnSharedPreferenceChangeListener(listener)

        val typeAdapter = TypeAdapter { type, checked ->
            pokemonViewModel.onTypeChange(type, checked)
        }
        binding.mainSearchTypes.adapter = typeAdapter
        binding.mainSearchTypes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val pokemonsAdapter = PokemonAdapter(
            { content, textView, imageView ->
                pokemonViewModel.onPokemonClicked(content, textView, imageView)
            }, {
                // nothing yet
            }
        )
        binding.mainRecyclerView.adapter = pokemonsAdapter
        val layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecyclerView.layoutManager = layoutManager
        binding.mainRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        pokemonViewModel.pokemonViewStateLiveData.observe(viewLifecycleOwner) { state ->
            pokemonsAdapter.submitList(state.items)
            typeAdapter.submitList(state.types.toList())
            binding.swiperefresh.isRefreshing = state.isRefreshing
        }
        pokemonViewModel.viewActions.observe(viewLifecycleOwner) {
            when (it) {
                is PokemonViewActions.NavigateToDetail -> {
                    val imageViewPair = Pair(it.imageView, it.imageView.transitionName)
                    val textViewPair = Pair(it.textView, it.textView.transitionName)

                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        imageViewPair,
                        textViewPair
                    )
                    startActivity(
                        DetailActivity.navigate(
                            requireContext(),
                            it.pokemonId,
                            it.pokemonId
                        ), options.toBundle()
                    )

                }
                is PokemonViewActions.NavigateToDetail2 -> TODO()
            }.exhaustive
        }

        binding.crashButton.setOnClickListener {
            Firebase.crashlytics.log("${javaClass.simpleName} : before crash")
            throw RuntimeException("Test Crash")
        }
        binding.crashButtonNonFatal.setOnClickListener {
            Firebase.crashlytics.log("${javaClass.simpleName} : before crash (non fatal)")
            Firebase.crashlytics.recordException(IllegalArgumentException("Test Crash Non Fatal"))
        }
        binding.crashButton.hide()
        binding.crashButtonNonFatal.hide()

        binding.swiperefresh.setOnRefreshListener {
            Log.d("refresh", "Refreshing...")
            pokemonViewModel.refresh()
            Log.d("refresh", "Refreshed !")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(listener)
    }

}
