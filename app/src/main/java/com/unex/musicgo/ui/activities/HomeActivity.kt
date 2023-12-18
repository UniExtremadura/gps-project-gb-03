package com.unex.musicgo.ui.activities

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.unex.musicgo.R
import com.unex.musicgo.databinding.DialogBinding
import com.unex.musicgo.databinding.GeneralActivityBinding
import com.unex.musicgo.models.PlayList
import com.unex.musicgo.models.PlayListWithSongs
import com.unex.musicgo.models.Song
import com.unex.musicgo.ui.fragments.FavoritesFragment
import com.unex.musicgo.ui.fragments.DiscoverFragment
import com.unex.musicgo.ui.fragments.HomeFragment
import com.unex.musicgo.ui.fragments.PlayListDetailsFragment
import com.unex.musicgo.ui.fragments.PlayListFragment
import com.unex.musicgo.ui.fragments.ProfileStatisticsFragment
import com.unex.musicgo.ui.interfaces.OnSearchListener
import com.unex.musicgo.ui.fragments.SearchFragment
import com.unex.musicgo.ui.fragments.SettingsFragment
import com.unex.musicgo.ui.fragments.SongDetailsFragment
import com.unex.musicgo.ui.fragments.StatisticsFragment
import com.unex.musicgo.ui.interfaces.OnCreatePlayListButtonClick
import com.unex.musicgo.ui.interfaces.OnSongClickListener
import com.unex.musicgo.ui.vms.HomeActivityViewModel
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), OnSongClickListener, OnSearchListener,
    ProfileStatisticsFragment.OnConsultStatisticsListener, OnCreatePlayListButtonClick,
    PlayListFragment.OnPlaylistClickListener, HomeFragment.OnDiscoverButtonClick {

    companion object {
        const val TAG = "HomeActivity"

        fun getIntent(context: AppCompatActivity) =
            android.content.Intent(context, HomeActivity::class.java)
    }

    private lateinit var binding: GeneralActivityBinding

    private val viewModel: HomeActivityViewModel by lazy {
        ViewModelProvider(
            this,
            HomeActivityViewModel.Factory
        )[HomeActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = GeneralActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpBinding()

        initViewModel()

        if (savedInstanceState == null) {
            launchHomeFragment(false)
        }
    }

    private fun initViewModel() {
        viewModel.toastLiveData.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.confirmImportPlayListLiveData.observe(this) { playlist ->
            if (playlist != null) {
                setUpImportPlayListDialog(playlist)
            }
        }

        val uri = intent?.data
        lifecycleScope.launch {
            viewModel.handlerUriData(uri)
        }
    }

    private fun setUpBinding() {
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
                        launchFavoritesFragment()
                        true
                    }

                    R.id.top_list -> {
                        launchPlayListFragment()
                        true
                    }

                    R.id.profile -> {
                        launchProfileStatistics()
                        true
                    }

                    else -> false
                }
            }
            customToolbar.homeIcon.setOnClickListener { launchHomeFragment() }
            customToolbar.settingsIcon.setOnClickListener { launchSettingsFragment() }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.clearCache()
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

    private fun launchProfileStatistics() {
        val fragment = ProfileStatisticsFragment.newInstance()
        replaceFragment(fragment)
    }

    private fun launchPlayListFragment() {
        val fragment = PlayListFragment.newInstance()
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
        val themedContext = ContextThemeWrapper(this@HomeActivity, R.style.PopupMenuStyleMusicGo)
        val popup = PopupMenu(themedContext, view)

        popup.menuInflater.inflate(R.menu.song_options_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_to_playlist -> {
                    // Launch the PlayListFragment with the song to add to a playlist
                    val fragment = PlayListFragment.addSongToPlayListInstance(song)
                    replaceFragment(fragment)
                    true
                }

                else -> false
            }
        }

        popup.show()
    }

    override fun onConsultStatistics() {
        val fragment = StatisticsFragment.newInstance()
        replaceFragment(fragment)
    }

    override fun onCreatePlayListButtonClick() {
        Log.d(TAG, "Create playlist button clicked")
        val fragment = PlayListDetailsFragment.newCreateInstance()
        replaceFragment(fragment)
    }

    override fun onPlayListClick(playlist: PlayList) {
        Log.d(TAG, "Playlist ${playlist.title} clicked")
        val fragment = PlayListDetailsFragment.newInstance(playlist)
        replaceFragment(fragment)
    }

    private fun setUpImportPlayListDialog(playlist: PlayListWithSongs) {
        // Create a new dialog and set the content view to the dialog layout
        val dialog = Dialog(this)
        val dialogBinding = DialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        with(dialogBinding) {
            this.dialogPlaylistName.text = playlist.playlist.title
            this.cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            this.confirmButton.setOnClickListener {
                dialog.dismiss()
                lifecycleScope.launch {
                    viewModel.importPlayList(playlist)
                }
            }
        }

        // Show the dialog
        dialog.show()
    }

    private fun launchFavoritesFragment() {
        val fragment = FavoritesFragment.newInstance()
        replaceFragment(fragment)
    }

    override fun onDiscoverButtonClick() {
        val fragment = DiscoverFragment.newInstance()
        replaceFragment(fragment)
    }

}