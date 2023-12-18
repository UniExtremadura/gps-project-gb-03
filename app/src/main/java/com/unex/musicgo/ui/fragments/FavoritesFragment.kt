package com.unex.musicgo.ui.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unex.musicgo.R
import com.unex.musicgo.databinding.FavoritesFragmentBinding
import com.unex.musicgo.ui.interfaces.OnSongClickListener
import com.unex.musicgo.ui.vms.FavoritesFragmentViewModel

class FavoritesFragment : Fragment() {

    companion object {
        private val TAG = "FavoritesFragment"
        fun newInstance() = FavoritesFragment()
    }

    private var _binding: FavoritesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: OnSongClickListener

    private val viewModel: FavoritesFragmentViewModel by lazy {
        ViewModelProvider(this)[FavoritesFragmentViewModel::class.java]
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
        binding.btn5.starsText.text = "5"
        binding.btn4.starsText.text = "4"
        binding.btn5.starsBtn.setOnClickListener {
            viewModel.updateStars(5)
        }
        binding.btn4.starsBtn.setOnClickListener {
            viewModel.updateStars(4)
        }
        launchFavoritesSongFragment()
        return binding.root
    }

    private fun launchFavoritesSongFragment(stars: Int? = null) {
        val songListFragment = SongListFragment.newFavoritesInstance(stars)
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(binding.fragmentSongListContainer.id, songListFragment)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewModel()
    }

    private fun setUpViewModel() {
        viewModel.stars.observe(viewLifecycleOwner) {
            when (it) {
                0 -> {
                    setColorBlue(binding.btn4.starsBtnContainer)
                    setColorBlue(binding.btn5.starsBtnContainer)
                    launchFavoritesSongFragment()
                }
                4 -> {
                    setColorBlue(binding.btn5.starsBtnContainer)
                    setColorGreen(binding.btn4.starsBtnContainer)
                    launchFavoritesSongFragment(4)
                }
                5 -> {
                    setColorBlue(binding.btn4.starsBtnContainer)
                    setColorGreen(binding.btn5.starsBtnContainer)
                    launchFavoritesSongFragment(5)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Set the bottom navigation item as selected
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigation.menu.getItem(2).isCheckable = true
        bottomNavigation.menu.getItem(2).isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}