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
    private var db: MusicGoDatabase? = null

    private var query: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate SongListFragment")
        arguments?.let {
            query = it.getString("query")
        }

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
    }

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
        if(query == null) throw RuntimeException("query must not be null")
        with(binding) {
            // Always create a new SongListFragment
            val songListFragment = SongListFragment.newSearchInstance(query!!)
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
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Set the bottom navigation item as selected
        with(binding) {
            recentSongsTitle.visibility = View.GONE
            searchView.setQuery(query, false)
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