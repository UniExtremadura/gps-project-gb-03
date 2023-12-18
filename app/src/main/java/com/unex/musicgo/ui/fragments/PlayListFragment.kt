package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unex.musicgo.R
import com.unex.musicgo.databinding.PlaylistListFragmentBinding
import com.unex.musicgo.models.PlayList
import com.unex.musicgo.models.Song
import com.unex.musicgo.ui.adapters.PlaylistListAdapter
import com.unex.musicgo.ui.interfaces.OnCreatePlayListButtonClick
import com.unex.musicgo.ui.vms.PlayListFragmentViewModel

class PlayListFragment : Fragment() {

    interface OnPlaylistClickListener {
        fun onPlayListClick(playlist: PlayList)
    }

    private var binding: PlaylistListFragmentBinding? = null

    private lateinit var listener: OnPlaylistClickListener
    private lateinit var onCreatePlayListClickListener: OnCreatePlayListButtonClick

    private lateinit var adapter: PlaylistListAdapter

    private val viewModel: PlayListFragmentViewModel by lazy {
        ViewModelProvider(
            this,
            PlayListFragmentViewModel.Factory
        )[PlayListFragmentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate SongListFragment")

        arguments?.let {
            val song: Song? = it.getSerializable(ARG_SONG) as Song?
            viewModel.setSong(song)
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach SongListFragment")
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
        Log.d(TAG, "onCreateView SongListFragment")
        binding = PlaylistListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
        setUpViews()
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding?.spinner?.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.playlists.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    private fun setUpViews() {
        setUpRecyclerView()
        binding?.createPlaylistBtn?.setOnClickListener {
            onCreatePlayListClickListener.onCreatePlayListButtonClick()
        }
    }

    private fun setUpRecyclerView() {
        binding?.let {
            adapter = PlaylistListAdapter(
                playlists = emptyList(),
                onPlayListClick = { playlist ->
                    if (viewModel.songIsNull()) listener.onPlayListClick(playlist)
                    else viewModel.addSongToPlayList(playlist) { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        // Go back to the song list
                        activity?.onBackPressed()
                    }
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
        // Set the selected item in the bottom navigation menu
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigation.menu.getItem(1).isCheckable = true
        bottomNavigation.menu.getItem(1).isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // avoid memory leaks
    }

    companion object {

        const val TAG = "PlayListFragment"
        const val ARG_SONG = "song"

        @JvmStatic
        fun newInstance() = PlayListFragment()

        @JvmStatic
        fun addSongToPlayListInstance(song: Song) = PlayListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_SONG, song)
            }
        }
    }
}