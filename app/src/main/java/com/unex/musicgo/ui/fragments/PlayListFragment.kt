package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unex.musicgo.R
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.PlaylistListFragmentBinding
import com.unex.musicgo.models.PlayList
import com.unex.musicgo.ui.adapters.PlaylistListAdapter
import com.unex.musicgo.ui.interfaces.OnCreatePlayListButtonClick

class PlayListFragment : Fragment() {

    private var _playlists: List<PlayList> = emptyList()

    private lateinit var listener: OnPlaylistClickListener
    private lateinit var onCreatePlayListClickListener: OnCreatePlayListButtonClick

    interface OnPlaylistClickListener {
        fun onPlayListClick(playlist: PlayList)
    }

    private var binding: PlaylistListFragmentBinding? = null
    private lateinit var adapter: PlaylistListAdapter

    private var db: MusicGoDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate PlayListFragment")

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach PlayListFragment")
        super.onAttach(context)
        if (context is OnPlaylistClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnPlaylistClickListener")
        }
        if (context is OnCreatePlayListButtonClick) {
            onCreatePlayListClickListener = context
        } else {
            throw RuntimeException("$context must implement OnCreatePlayListButtonClick")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView PlayListFragment")
        binding = PlaylistListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        lifecycleScope.launch {
            binding?.let {
                it.spinner.visibility = View.VISIBLE
                it.createPlaylistBtn.setOnClickListener {
                    onCreatePlayListClickListener.onCreatePlayListButtonClick()
                }
                try {
                    _playlists =
                        db?.playListDao()?.getPlayListsCreatedByUserWithoutSongs() ?: emptyList()
                    adapter.updateData(_playlists)
                } catch (error: Error) {
                    context?.let {
                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    it.spinner.visibility = View.GONE
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        binding?.let {
            adapter = PlaylistListAdapter(
                playlists = _playlists,
                onPlayListClick = {
                    listener.onPlayListClick(it)
                },
                context = this.context
            )
            it.recyclerView.layoutManager = GridLayoutManager(context, 2)
            it.recyclerView.adapter = adapter
        }
        Log.d(TAG, "setUpRecyclerView")
    }

    override fun onStart() {
        super.onStart()
        with(binding) {
            val bottomNavigation =
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigation?.let {
                it.menu.getItem(1).isCheckable = true
                it.menu.getItem(1).isChecked = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // avoid memory leaks
    }

    companion object {

        const val TAG = "PlayListFragment"

        @JvmStatic
        fun newInstance() = PlayListFragment()
    }
}