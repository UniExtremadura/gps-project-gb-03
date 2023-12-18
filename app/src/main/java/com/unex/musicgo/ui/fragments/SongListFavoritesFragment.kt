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
import androidx.recyclerview.widget.LinearLayoutManager
import com.unex.musicgo.databinding.SongListFragmentBinding
import com.unex.musicgo.ui.adapters.SongListFavoritesAdapter
import com.unex.musicgo.ui.interfaces.OnSongClickListener
import com.unex.musicgo.ui.vms.SongListFavoritesFragmentViewModel

class SongListFavoritesFragment : Fragment() {

    private lateinit var listener: OnSongClickListener

    private var binding: SongListFragmentBinding? = null
    private lateinit var adapter: SongListFavoritesAdapter

    private val viewModel: SongListFavoritesFragmentViewModel by lazy {
        ViewModelProvider(
            this,
            SongListFavoritesFragmentViewModel.Factory
        )[(SongListFavoritesFragmentViewModel::class.java)]
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach SongListFavoritesFragment")
        super.onAttach(context)
        if (context is OnSongClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSongClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView SongListFavoritesFragment")
        binding = SongListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
        setUpRecyclerView()

        viewModel.load(requireContext()) {
            viewModel.fetchMostPlayedSongs()
        }
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding?.spinner?.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.listMostPlayedStatistics.observe(viewLifecycleOwner) {
            viewModel.load(requireContext()) {
                viewModel.fetchSongs()
            }
        }
        viewModel.songs.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    private fun setUpRecyclerView() {
        binding?.let {
            adapter = SongListFavoritesAdapter(
                songs = emptyList(),
                onClick = { song ->
                    listener.onSongClick(song)
                },
                onOptionsClick = { song, view ->
                    listener.onOptionsClick(song, view)
                },
                context = this.context
            )
            it.rvSongList.layoutManager = LinearLayoutManager(context)
            it.rvSongList.adapter = adapter
        }
        Log.d(TAG, "setUpRecyclerView")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // avoid memory leaks
    }

    companion object {

        const val TAG = "SongListFavoritesFragment"

        @JvmStatic
        fun newInstance() = SongListFavoritesFragment()
    }
}