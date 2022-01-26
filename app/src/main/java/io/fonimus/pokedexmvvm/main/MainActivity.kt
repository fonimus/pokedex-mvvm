package io.fonimus.pokedexmvvm.main

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import io.fonimus.pokedexmvvm.R
import io.fonimus.pokedexmvvm.data.MessageWrapper
import io.fonimus.pokedexmvvm.databinding.HeaderDrawerBinding
import io.fonimus.pokedexmvvm.databinding.MainActivityBinding
import io.fonimus.pokedexmvvm.settings.SettingsFragment
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            Log.d("prefs", "$key changed in $sharedPreferences")
        }
    private lateinit var binding: MainActivityBinding

    private val mainFragment = MainFragment.newInstance()
    private val settingsFragment = SettingsFragment.newInstance()
    private var selectedItem: NavigationItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Init view binding
        binding = MainActivityBinding.inflate(layoutInflater)
        HeaderDrawerBinding.bind(binding.navigationView.getHeaderView(0))
        // Setup UI
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_navigation_home -> openFragment(NavigationItem.HOME)
                R.id.bottom_navigation_settings -> openFragment(NavigationItem.SETTINGS)
            }
            true
        }
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_home -> openFragment(NavigationItem.HOME)
                R.id.drawer_settings -> openFragment(NavigationItem.SETTINGS)
            }
            true
        }

        ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.description_open_drawer,
            R.string.description_close_drawer
        ).syncState()

        supportFragmentManager.beginTransaction()
            .add(binding.fragmentContainer.id, settingsFragment).detach(settingsFragment)
            .commit()
        supportFragmentManager.beginTransaction()
            .add(binding.fragmentContainer.id, mainFragment)
            .commit()


        if (savedInstanceState == null) {
            openFragment(NavigationItem.HOME)
        }

        // firebase remote config
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val updated = task.result
                Log.d("firebase", "Config params updated: $updated")
                Log.d("firebase", remoteConfig.all.toString())
                val iconType = remoteConfig.getString("icon_type")
                Toast.makeText(
                    this, "Fetch and activate succeeded (icon=$iconType)",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, "Fetch failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // firestore database
        Firebase.firestore.collection("test").document("testId")
            .addSnapshotListener { doc, error ->
                Log.d("firestore", "Key: ${doc?.toObject<MessageWrapper>()?.key}")
                if (error != null) {
                    Firebase.crashlytics.recordException(error)
                }
            }
    }

    private fun openFragment(item: NavigationItem) {
        if (selectedItem != item) {
            selectedItem = item
            when (item) {
                NavigationItem.HOME -> {
                    binding.bottomNavigation.selectedItemId = R.id.bottom_navigation_home
                    binding.navigationView.setCheckedItem(R.id.drawer_home)

                    supportFragmentManager.beginTransaction()
                        .attach(mainFragment).detach(settingsFragment)
                        .commit()
                }
                NavigationItem.SETTINGS -> {
                    binding.bottomNavigation.selectedItemId = R.id.bottom_navigation_settings
                    binding.navigationView.setCheckedItem(R.id.drawer_settings)

                    supportFragmentManager.beginTransaction()
                        .attach(settingsFragment).detach(mainFragment)
                        .commit()
                }
            }
        }

        closeDrawer()
    }

    private fun closeDrawer(): Boolean {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        if (!closeDrawer()) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(listener)
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
                viewModel.onQueryTextChange(query)
                return true
            }
        })

        menu.findItem(R.id.open_settings).setOnMenuItemClickListener {
            openFragment(NavigationItem.SETTINGS)
            true
        }
        return true
    }
}
