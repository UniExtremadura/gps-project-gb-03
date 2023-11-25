package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unex.musicgo.R
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.HomeFragmentBinding
import com.unex.musicgo.ui.interfaces.OnSearchListener
import com.unex.musicgo.ui.interfaces.OnSongClickListener
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var onSongClickListener: OnSongClickListener
    private lateinit var onSearchListener: OnSearchListener
    private lateinit var onDiscoverButtonClick: OnDiscoverButtonClick

    private var db: MusicGoDatabase? = null


    interface OnDiscoverButtonClick {
        fun onDiscoverButtonClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate SongListFragment")
        arguments?.let {

        }

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach SongListFragment")
        super.onAttach(context)

        // Check if the context implements SongListFragment.OnSongClickListener
        if (context is OnSongClickListener) {
            onSongClickListener = context
        } else {
            throw RuntimeException("$context must implement OnSongClickListener")
        }

        // Check if the context implements OnSearchListener
        if (context is OnSearchListener) {
            onSearchListener = context
        } else {
            throw RuntimeException("$context must implement OnSearchListener")
        }

        // Check if the context implements OnDiscoverButtonClick
        if (context is OnDiscoverButtonClick) {
            onDiscoverButtonClick = context
        } else {
            throw RuntimeException("$context must implement OnDiscoverButtonClick")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView SongListFragment")
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        with(binding) {
            // Always create a new SongListFragment
            val songListFragment = SongListFragment.newRecentsInstance()
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(this.fragmentSongListContainer.id, songListFragment)
            transaction.commit()

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { onSearchListener.onSearch(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })

            discoverButton.setOnClickListener {
                onDiscoverButtonClick.onDiscoverButtonClick()
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Set the bottom navigation item as selected
        with(binding) {
            val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigation?.let {
                it.menu.getItem(0).isCheckable = true
                it.menu.getItem(0).isChecked = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}