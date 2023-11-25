package com.unex.musicgo.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.unex.musicgo.databinding.SongListFragmentBinding
import com.unex.musicgo.models.Song
import com.unex.musicgo.ui.adapters.SongListAdapter
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.unex.musicgo.api.getAuthToken
import com.unex.musicgo.api.getNetworkService
import com.unex.musicgo.data.api.common.Items
import com.unex.musicgo.data.toSong
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.models.PlayListWithSongs
import com.unex.musicgo.ui.interfaces.OnSongClickListener

class SongListFragment : Fragment() {

    private val TAG = "SongListFragment"
    private var _songs: List<Song> = emptyList()

    private lateinit var listener: OnSongClickListener
    private var onDeleteListener: OnSongDeleteListener? = null

    /**
     * Private enum class to handle the options:
     *
     * - recommendations
     * - recents
     */
    private enum class Option {
        RECENT,
        SEARCH,
        PLAYLIST,
        FAVORITES,
        RECOMMENDATION
    }

    interface OnSongDeleteListener {
        fun onSongDelete(song: Song)
    }

    private var _query: String? = null
    private val query get() = _query
    private var stars: Int? = null

    private var _option: String? = null
    private var playList: PlayListWithSongs? = null
    private val option get() = _option

    private var binding: SongListFragmentBinding? = null
    private lateinit var adapter: SongListAdapter

    private var db: MusicGoDatabase? = null

    private var genreSeed: String? = null
    private var artistSeed: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate SongListFragment")
        arguments?.let {
            _query = it.getString(QUERY_ARG)
            _option = it.getString(OPTION_ARG)
            stars = it.getInt(OPTION_STARS)
            genreSeed = it.getString(GENRE_ARG)
            artistSeed = it.getString(ARTIST_ARG)
            playList = arguments?.getSerializable(PLAYLIST_ARG) as PlayListWithSongs?
        }

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
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
        lifecycleScope.launch {
            binding?.let {
                it.spinner.visibility = View.VISIBLE
                try {
                    _songs = if (option == Option.SEARCH.name) {
                        searchSongs()
                    } else if (option == Option.RECENT.name) {
                        fetchRecentSongs()
                    } else if (option == Option.PLAYLIST.name) {
                        playList!!.songs
                    } else if (option == Option.FAVORITES.name) {
                        fetchFavoritesSongs()
                    } else if (option == Option.RECOMMENDATION.name) {
                        fetchRecommendations()
                    } else {
                        throw IllegalArgumentException("Option not found")
                    }
                    adapter.updateData(_songs)
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: Network? = connectivityManager.activeNetwork
        val isConnected: Boolean = activeNetwork != null
        Log.d(TAG, "isConnected: $isConnected")
        return isConnected
    }

    private suspend fun fetchRecommendations(): List<Song> {
        if (genreSeed == null && artistSeed == null) {
            throw Error("Genre seed and artist seed cannot be null")
        }
        Log.d(
            TAG,
            "Fetching recommendations from API with genre seed: $genreSeed and artist seed: $artistSeed"
        )
        var listOfSongs: List<Song>
        // Get the songs from the network
        val service = getNetworkService()
        val authToken = getAuthToken()
        val recommendations = if (artistSeed.isNullOrEmpty() && genreSeed.isNullOrEmpty()) {
            service.getRecommendations(authToken, limit = 50)
        } else {
            service.getRecommendations(
                authToken,
                limit = 50,
                seedTracks = "",
                seedArtists = artistSeed ?: "",
                seedGenres = genreSeed ?: ""
            )
        }
        // Get the tracks from recommendations
        val tracks = recommendations.tracks
        // Mapped from Tracks API to Song
        listOfSongs = tracks.map(Items::toSong)
        listOfSongs = listOfSongs.filter { song -> song.previewUrl != null }
        return listOfSongs
    }

    private suspend fun fetchRecentSongs(): List<Song> =
        db?.playListDao()?.getRecentPlayList()?.songs ?: emptyList()

    private suspend fun fetchFavoritesSongs(): List<Song> {
        val favoritesPlayList = db?.playListDao()?.getFavoritesPlayList()
            ?: throw Error("Favorites not found")
        var songs = favoritesPlayList.songs
        stars?.let {
            if (stars == 4 || stars == 5) {
                songs = favoritesPlayList.songs.filter {
                    Log.d(TAG, "Rating: ${it.rating}")
                    Log.d(TAG, "Stars: $stars")
                    it.rating == stars
                }
            }
        }
        Log.d(TAG, "Favorites: $songs")
        return songs
    }

    private suspend fun searchSongs(): List<Song> {
        if (query == null) {
            throw Error("Query cannot be null")
        }
        val listOfSongs: List<Song>
        // Get the songs from the database
        val db = MusicGoDatabase.getInstance(requireContext())
        val songsOnDB = db?.songsDao()?.searchByQuery(query!!)
        // Check the connection
        if (isNetworkAvailable()) {
            // Get auth token
            val authToken = getAuthToken()
            Log.d(TAG, "Auth token: $authToken")
            Log.d(TAG, "Query: $query")
            // Get the songs from the API
            val songs = getNetworkService().search(authToken, query!!)
            // Get the items from the songs
            val items = songs.tracks?.items
            Log.d(TAG, "Items: $items")
            // Get the songs from the items
            val fetchedSongs = items?.map(Items::toSong)
                ?: throw Error("Unable to fetch data from API", null)
            // Remove the songs with no preview url
            listOfSongs = fetchedSongs.filter { song -> song.previewUrl != null }
            // Save the songs to the database
            db?.songsDao()?.insertAll(listOfSongs)
        } else if (!isNetworkAvailable() && songsOnDB.isNullOrEmpty()) {
            throw Error("No internet connection")
        } else {
            // Set the database songs to the list of songs
            listOfSongs = songsOnDB!!
        }
        return listOfSongs
    }

    private fun setUpRecyclerView() {
        binding?.let {
            adapter = SongListAdapter(
                songs = _songs,
                onClick = {
                    listener.onSongClick(it)
                },
                onOptionsClick = { song, view ->
                    listener.onOptionsClick(song, view)
                },
                onDeleteClick = {
                    onDeleteListener?.onSongDelete(it)
                },
                showTrash = option == Option.PLAYLIST.name,
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

    class Error(message: String, cause: Throwable?) : Throwable(message, cause)

    companion object {

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
                        putString(OPTION_ARG, Option.SEARCH.name)
                        putString(QUERY_ARG, query)
                    }
                }
        }

        @JvmStatic
        fun newRecentsInstance(): SongListFragment {
            return SongListFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(OPTION_ARG, Option.RECENT.name)
                    }
                }
        }

        @JvmStatic
        fun newPlayListInstance(playList: PlayListWithSongs): SongListFragment {
            return SongListFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(OPTION_ARG, Option.PLAYLIST.name)
                        putSerializable(PLAYLIST_ARG, playList)
                    }
                }
        }

        @JvmStatic
        fun newFavoritesInstance(stars: Int? = null): SongListFragment {
            return SongListFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(OPTION_ARG, Option.FAVORITES.name)
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
                        putString(OPTION_ARG, Option.RECOMMENDATION.name)
                        putString(GENRE_ARG, genreSeed)
                        putString(ARTIST_ARG, artistSeed)
                    }
                }
        }
    }
}