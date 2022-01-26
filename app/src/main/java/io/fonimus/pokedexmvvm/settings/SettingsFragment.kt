package io.fonimus.pokedexmvvm.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.fonimus.pokedexmvvm.R

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance() = SettingsFragment().apply {
            arguments = Bundle().apply { }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
