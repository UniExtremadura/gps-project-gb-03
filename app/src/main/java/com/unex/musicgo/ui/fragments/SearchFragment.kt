package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unex.musicgo.R
import com.unex.musicgo.databinding.HomeFragmentBinding
import com.unex.musicgo.ui.interfaces.OnSearchListener
import com.unex.musicgo.ui.interfaces.OnSongClickListener

class SearchFragment : Fragment() {

    private val TAG = "SearchFragment"

    companion object {
        fun newInstance(query: String) = SearchFragment().apply {
            arguments = Bundle().apply {
                putString("query", query)
            }
        }
    }

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var onSongClickListener: OnSongClickListener
    private lateinit var onSearchListener: OnSearchListener

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach SongListFragment")
        super.onAttach(context)
        if (context is OnSongClickListener) {
            onSongClickListener = context
        } else {
            throw RuntimeException("$context must implement OnSongClickListener")
        }
        if (context is OnSearchListener) {
            onSearchListener = context
        } else {
            throw RuntimeException("$context must implement OnSearchListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView SearchFragment")
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        // Always create a new SongListFragment
        val query = arguments?.getString("query") ?: throw RuntimeException("query cannot be null")
        val songListFragment = SongListFragment.newSearchInstance(query)
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(binding.fragmentSongListContainer.id, songListFragment)
        transaction.commit()
        /** Set the search view listener */
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { onSearchListener.onSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onStart() {
        super.onStart()
        binding.recentSongsTitle.visibility = View.GONE
        binding.searchView.setQuery(arguments?.getString("query"), false)
        // Set the bottom navigation item as selected
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigation?.let {
            it.menu.getItem(0).isCheckable = true
            it.menu.getItem(0).isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}