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
import com.unex.musicgo.models.Song
import com.unex.musicgo.ui.adapters.SongListAdapter
import com.unex.musicgo.models.PlayListWithSongs
import com.unex.musicgo.ui.enums.SongListFragmentOption
import com.unex.musicgo.ui.interfaces.OnSongClickListener
import com.unex.musicgo.ui.vms.SongListFragmentViewModel

class SongListFragment : Fragment() {

    private var binding: SongListFragmentBinding? = null
    private lateinit var adapter: SongListAdapter

    private lateinit var listener: OnSongClickListener
    private var onDeleteListener: OnSongDeleteListener? = null

    interface OnSongDeleteListener {
        fun onSongDelete(song: Song)
    }

    private val viewModel: SongListFragmentViewModel by lazy {
        ViewModelProvider(this, SongListFragmentViewModel.Factory)[SongListFragmentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate SongListFragment")

        arguments?.let {
            viewModel.setQuery(it.getString(QUERY_ARG))
            viewModel.setStars(it.getInt(OPTION_STARS))
            viewModel.setRecommendationSeeds(it.getString(GENRE_ARG), it.getString(ARTIST_ARG))
            val playList = arguments?.getSerializable(PLAYLIST_ARG) as PlayListWithSongs?
            viewModel.setPlayList(playList)
            viewModel.setOption(
                SongListFragmentOption.valueOf(it.getString(OPTION_ARG)!!)
            )
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach SongListFragment")
        super.onAttach(context)

        if (context is OnSongClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSongClickListener")
        }

        if (parentFragment is OnSongDeleteListener) {
            Log.d(TAG, "OnSongDeleteListener")
            Log.d(TAG, "Parent fragment: $parentFragment")
            onDeleteListener = parentFragment as OnSongDeleteListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView SongListFragment")
        binding = SongListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpViewModel()
    }

    private fun setUpViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding?.spinner?.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.songs.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
        viewModel.canFetch.observe(viewLifecycleOwner) {
            viewModel.load(requireContext()) {
                viewModel.fetch()
            }
        }
    }

    private fun setUpRecyclerView() {
        binding?.let {
            adapter = SongListAdapter(
                songs = emptyList(),
                onClick = {
                    listener.onSongClick(it)
                },
                onOptionsClick = { song, view ->
                    listener.onOptionsClick(song, view)
                },
                onDeleteClick = {
                    onDeleteListener?.onSongDelete(it)
                },
                showTrash = viewModel.showTrash(),
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

        const val TAG = "SongListFragment"

        private const val OPTION_ARG = "option"
        private const val QUERY_ARG = "query"
        private const val OPTION_STARS = "stars"
        private const val PLAYLIST_ARG = "playList"
        private const val GENRE_ARG = "genre"
        private const val ARTIST_ARG = "artist"

        @JvmStatic
        fun newSearchInstance(query: String): SongListFragment {
            return SongListFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(OPTION_ARG, SongListFragmentOption.SEARCH.name)
                        putString(QUERY_ARG, query)
                    }
                }
        }

        @JvmStatic
        fun newRecentsInstance(): SongListFragment {
            return SongListFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(OPTION_ARG, SongListFragmentOption.RECENT.name)
                    }
                }
        }

        @JvmStatic
        fun newPlayListInstance(playList: PlayListWithSongs): SongListFragment {
            return SongListFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(OPTION_ARG, SongListFragmentOption.PLAYLIST.name)
                        putSerializable(PLAYLIST_ARG, playList)
                    }
                }
        }

        @JvmStatic
        fun newFavoritesInstance(stars: Int? = null): SongListFragment {
            return SongListFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(OPTION_ARG, SongListFragmentOption.FAVORITES.name)
                        putInt(OPTION_STARS, stars ?: 0)
                    }
                }
        }

        @JvmStatic
        fun newRecommendationsInstance(
            genreSeed: String? = null,
            artistSeed: String? = null
        ): SongListFragment {
            return SongListFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(OPTION_ARG, SongListFragmentOption.RECOMMENDATION.name)
                        putString(GENRE_ARG, genreSeed)
                        putString(ARTIST_ARG, artistSeed)
                    }
                }
        }
    }
}