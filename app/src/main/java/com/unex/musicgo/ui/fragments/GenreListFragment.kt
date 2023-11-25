package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.unex.musicgo.databinding.SongListFragmentBinding
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.unex.musicgo.api.getAuthToken
import com.unex.musicgo.api.getNetworkService
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.models.Genre
import com.unex.musicgo.ui.adapters.GenreListAdapter

class GenreListFragment : Fragment() {

    private val TAG = "GenreListFragment"
    private var _genres: List<Genre> = emptyList()

    private lateinit var listener: OnGenreClickListener

    interface OnGenreClickListener {
        fun onGenreClick(genre: Genre)
    }

    private var binding: SongListFragmentBinding? = null
    private lateinit var adapter: GenreListAdapter

    private var db: MusicGoDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate GenreListFragment")

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach GenreListFragment")
        super.onAttach(context)
        if (parentFragment is OnGenreClickListener) {
            listener = parentFragment as OnGenreClickListener
        } else if (context is OnGenreClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnGenreClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView GenreListFragment")
        binding = SongListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        lifecycleScope.launch {
            binding?.let {
                it.spinner.visibility = View.VISIBLE
                try {
                    _genres = fetchGenres()
                    adapter.updateData(_genres)
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

    private suspend fun fetchGenres(): List<Genre> {
        val networkService = getNetworkService()
        val authToken = getAuthToken()
        val response = networkService.getAvailableGenres(authToken)
        val genres = response.genres.map {
            Genre(it)
        }
        return genres
    }

    private fun setUpRecyclerView() {
        binding?.let {
            adapter = GenreListAdapter(
                genres = _genres,
                onClick = {
                    listener.onGenreClick(it)
                },
                context = this.context
            )
            it.rvSongList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            it.rvSongList.adapter = adapter
        }
        Log.d(TAG, "setUpRecyclerView")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // avoid memory leaks
    }

    class Error(message: String, cause: Throwable?) : Throwable(message, cause)

    companion object {

        @JvmStatic
        fun newInstance() = GenreListFragment()
    }
}