package com.unex.musicgo.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.unex.musicgo.R
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.GeneralActivityBinding
import com.unex.musicgo.models.Song
import com.unex.musicgo.ui.fragments.HomeFragment
import com.unex.musicgo.ui.fragments.SearchFragment
import com.unex.musicgo.ui.fragments.SongDetailsFragment
import com.unex.musicgo.ui.fragments.SettingsFragment
import com.unex.musicgo.ui.fragments.SongListFragment
import com.unex.musicgo.ui.interfaces.OnSearchListener
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), SongListFragment.OnSongClickListener, OnSearchListener {

    companion object {
        const val TAG = "HomeActivity"

        fun getIntent(context: AppCompatActivity) =
            android.content.Intent(context, HomeActivity::class.java)
    }

    private lateinit var binding: GeneralActivityBinding
    private var db: MusicGoDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = GeneralActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setSupportActionBar(customToolbar.root)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            bottomNavigationView.bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_home -> {
                        launchHomeFragment()
                        true
                    }

                    R.id.favourite -> {
                        true
                    }

                    R.id.top_list -> {
                        true
                    }

                    R.id.profile -> {
                        true
                    }

                    else -> false
                }
            }

            customToolbar.homeIcon.setOnClickListener {
                launchHomeFragment()
            }

            customToolbar.settingsIcon.setOnClickListener {
                launchSettingsFragment()
            }
        }

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(this@HomeActivity)
        }

        if (savedInstanceState == null) {
            launchHomeFragment(false)
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            lifecycleScope.launch {
                db?.songsDao()?.clearCache()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting old cache", e)
        }
    }

    private fun replaceFragment(
        fragment: Fragment,
        addToBackStack: Boolean = true,
        forceCreateNewFragment: Boolean = false
    ) {
        with(binding) {
            val currentFragment = supportFragmentManager.findFragmentById(this.fragmentContainer.id)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(this.fragmentContainer.id, fragment)
            if (addToBackStack) {
                val notIsCurrentFragment =
                    (currentFragment == null || currentFragment::class != fragment::class)
                if (forceCreateNewFragment || notIsCurrentFragment) {
                    transaction.addToBackStack(null)
                }
            }
            transaction.commit()
        }
    }

    private fun launchHomeFragment(addToBackStack: Boolean = true) {
        val fragment = HomeFragment.newInstance()
        replaceFragment(fragment, addToBackStack)
    }

    private fun launchSettingsFragment() {
        val fragment = SettingsFragment.newInstance()
        replaceFragment(fragment)
    }

    override fun onSearch(query: String) {
        Log.d(TAG, "Searching $query")
        val fragment = SearchFragment.newInstance(query)
        replaceFragment(fragment, forceCreateNewFragment = true)
    }

    override fun onSongClick(song: Song) {
        val fragment = SongDetailsFragment.newInstance(song)
        replaceFragment(fragment)
    }

    override fun onOptionsClick(song: Song, view: View) {
        // Not yet implemented
        Toast.makeText(
            this,
            "Not implemented yet",
            Toast.LENGTH_SHORT
        ).show()
    }

}