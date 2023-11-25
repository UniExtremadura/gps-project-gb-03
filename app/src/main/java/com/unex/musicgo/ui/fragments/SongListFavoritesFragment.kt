package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.unex.musicgo.models.Song
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.unex.musicgo.api.getAuthToken
import com.unex.musicgo.api.getNetworkService
import com.unex.musicgo.data.toSong
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.SongListFragmentBinding
import com.unex.musicgo.ui.adapters.SongListFavoritesAdapter
import com.unex.musicgo.ui.interfaces.OnSongClickListener

class SongListFavoritesFragment : Fragment() {

    private val TAG = "SongListFavoritesFragment"
    private var _songs: List<Song> = emptyList()

    private lateinit var listener: OnSongClickListener

    private var binding: SongListFragmentBinding? = null
    private lateinit var adapter: SongListFavoritesAdapter

    private var db: MusicGoDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate SongListFavoritesFragment")

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
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
        setUpRecyclerView()
        lifecycleScope.launch {
            binding?.let {
                it.spinner.visibility = View.VISIBLE
                try {
                    _songs = fetchSongs()
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

    private fun setUpRecyclerView() {
        binding?.let {
            adapter = SongListFavoritesAdapter(
                songs = _songs,
                onClick = {
                    listener.onSongClick(it)
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

    private suspend fun fetchSongs(): List<Song> {
        val songs = mutableListOf<Song>()
        // Get the songs most played
        val listMostPlayedStatistics = db?.statisticsDao()?.getAllMostPlayedSong(3)
        // Get the songs from database or network
        val service = getNetworkService()
        var token: String? = null
        listMostPlayedStatistics?.let {
            it.forEach {
                val songOnDB = db?.songsDao()?.getSongById(it.songId)
                if (songOnDB != null) {
                    songs.add(songOnDB)
                } else {
                    if (token == null) token = getAuthToken()
                    val songOnNetwork = service.getTrack(token!!, it.songId)
                    val song = songOnNetwork.toSong()
                    songs.add(song)
                    db?.songsDao()?.insert(song)
                }
            }
        }
        return songs
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // avoid memory leaks
    }

    class Error(message: String, cause: Throwable?) : Throwable(message, cause)

    companion object {

        @JvmStatic
        fun newInstance() = SongListFavoritesFragment()
    }
}