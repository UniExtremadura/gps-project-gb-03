package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.unex.musicgo.api.getAuthToken
import com.unex.musicgo.api.getNetworkService
import com.unex.musicgo.data.api.search.SearchResponse
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.DiscoverFragmentBinding
import com.unex.musicgo.models.Genre
import com.unex.musicgo.ui.interfaces.OnSongClickListener
import kotlinx.coroutines.launch

class DiscoverFragment : Fragment(), GenreListFragment.OnGenreClickListener {

    private val TAG = "DiscoverFragment"

    companion object {
        fun newInstance() = DiscoverFragment()
    }

    private var _binding: DiscoverFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var onSongClickListener: OnSongClickListener
    private var db: MusicGoDatabase? = null

    private var genresSelected: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate DiscoverFragment")

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach DiscoverFragment")
        super.onAttach(context)
        if (context is OnSongClickListener) {
            onSongClickListener = context
        } else {
            throw RuntimeException("$context must implement OnSongClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView DiscoverFragment")
        _binding = DiscoverFragmentBinding.inflate(inflater, container, false)
        with(binding) {
            val genresFragment = GenreListFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(this.fragmentGenreListContainer.id, genresFragment)
                .commit()
            launchRecommendationsFragment()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            this.btnApplyFilters.setOnClickListener {
                lifecycleScope.launch {
                    val artist = searchBar.query.toString()
                    val artistSeed = fetchArtistSeed(artist)
                    launchRecommendationsFragment(genresSelected, artistSeed)
                }
            }
        }
    }

    private fun launchRecommendationsFragment(genres: List<String>? = null, artist: String = "") {
        Log.d(TAG, "launchRecommendationsFragment")
        val genresString = genres?.joinToString(separator = "%2C") ?: ""
        val fragment = SongListFragment.newRecommendationsInstance(genresString, artist)
        childFragmentManager.beginTransaction()
            .replace(binding.fragmentSongListContainer.id, fragment)
            .commit()
    }

    private suspend fun fetchArtistSeed(artist: String? = ""): String {
        Log.d(TAG, "fetchArtistSeed")
        if (artist.isNullOrEmpty()) return ""
        val service = getNetworkService()
        val auth = getAuthToken()
        val response: SearchResponse =
            service.search(auth = auth, query = artist, type = "artist", limit = 1)
        val artistId = response.artists?.items?.get(0)?.id
        Log.d(TAG, "Artist id: $artistId")
        return artistId ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

    override fun onGenreClick(genre: Genre) {
        // If the genre is already selected, remove it from the list
        genresSelected = if (genresSelected.contains(genre.title)) {
            genresSelected.minus(genre.title)
        } else {
            // If the genre is not selected, add it to the list
            genresSelected.plus(genre.title)
        }
    }

}