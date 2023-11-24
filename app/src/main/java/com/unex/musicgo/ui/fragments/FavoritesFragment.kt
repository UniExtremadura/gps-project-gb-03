package com.unex.musicgo.ui.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unex.musicgo.R
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.FavoritesFragmentBinding
import com.unex.musicgo.ui.interfaces.OnSongClickListener
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    companion object {
        private val TAG = "FavoritesFragment"
        fun newInstance() = FavoritesFragment()
    }

    private var _binding: FavoritesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: OnSongClickListener
    private var db: MusicGoDatabase? = null

    private var starsFilter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate favoritesFragment")

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach favoritesFragment")
        super.onAttach(context)
        if (context is OnSongClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSongClickListener")
        }
    }

    private fun setColorGreen(btn: View) {
        btn.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(
                R.color.custom_green,
                null
            )
        )
    }

    private fun setColorBlue(btn: View) {
        btn.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(
                R.color.blue,
                null
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView favoritesFragment")
        _binding = FavoritesFragmentBinding.inflate(inflater, container, false)
        with(binding) {
            this.btn5.starsText.text = "5"
            this.btn4.starsText.text = "4"
            this.btn5.starsBtn.setOnClickListener {
                if (starsFilter == 5) {
                    setColorBlue(btn4.starsBtnContainer)
                    setColorBlue(btn5.starsBtnContainer)
                    starsFilter = 0
                } else {
                    setColorBlue(btn4.starsBtnContainer)
                    setColorGreen(btn5.starsBtnContainer)
                    starsFilter = 5
                }
                filterByStars(starsFilter)
            }
            this.btn4.starsBtn.setOnClickListener {
                if (starsFilter == 4) {
                    setColorBlue(btn5.starsBtnContainer)
                    setColorBlue(btn4.starsBtnContainer)
                    starsFilter = 0
                } else {
                    setColorBlue(btn5.starsBtnContainer)
                    setColorGreen(btn4.starsBtnContainer)
                    starsFilter = 4
                }
                filterByStars(starsFilter)
            }
            val songListFragment = SongListFragment.newFavoritesInstance()
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(this.fragmentSongListContainer.id, songListFragment)
            transaction.commit()
        }
        return binding.root
    }

    private fun filterByStars(stars: Int) {
        Log.d(TAG, "filterByStars favoritesFragment")
        val songListFragment = SongListFragment.newFavoritesInstance(stars)
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(binding.fragmentSongListContainer.id, songListFragment)
        transaction.commit()
    }

    override fun onStart() {
        super.onStart()
        // Set the bottom navigation item as selected
        with(binding) {
            val bottomNavigation =
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigation?.let {
                it.menu.getItem(2).isCheckable = true
                it.menu.getItem(2).isChecked = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}