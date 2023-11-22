package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.unex.musicgo.api.getAuthToken
import com.unex.musicgo.api.getNetworkService
import com.unex.musicgo.data.toSong
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.SongDetailsFragmentBinding
import com.unex.musicgo.models.Song
import kotlinx.coroutines.launch

class SongDetailsFragment : Fragment() {
    companion object {
        const val TAG = "SongDetailsFragment"
        private const val EXTRA_TRACK_ID = "track_id"

        @JvmStatic
        fun newInstance(song: Song) = SongDetailsFragment().apply {
            arguments = Bundle().apply {
                Log.d(TAG, "newInstance song: $song")
                putString(EXTRA_TRACK_ID, song.id)
            }
        }
    }

    private var _binding: SongDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private var db: MusicGoDatabase? = null

    private var trackId: String? = null
    private var song: Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate SongDetailsFragment")
        arguments?.let {
            trackId = it.getString(EXTRA_TRACK_ID)
        }

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach SongDetailsFragment")
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView SongDetailsFragment")
        _binding = SongDetailsFragmentBinding.inflate(inflater, container, false)

        if (savedInstanceState != null) {
            song = savedInstanceState.getSerializable("song") as Song?
            trackId = savedInstanceState.getString("trackId")
            bindSong()
        } else if (trackId == null) {
            Toast.makeText(requireContext(), "No track id", Toast.LENGTH_SHORT).show()
        } else {
            initialState()
        }

        return binding.root
    }

    private fun initialState() {
        try {
            lifecycleScope.launch {
                Log.d(TAG, "SongBaseModel id: $trackId")
                song = fetchSong(trackId!!)
                Log.d(TAG, "Song: $song")
                bindSong()
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: $e")
            Toast.makeText(requireContext(), "Error while fetching song", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun fetchSong(songId: String): Song {
        var song: Song?
        try {
            // Try to get the song from the database cache
            val db = MusicGoDatabase.getInstance(requireContext())
            song = db?.songsDao()?.getSongById(songId)
            // If the song is not in the database, get it from the network
            if (song == null) {
                // Get the song from the network
                song = getTrack(songId)
                // Insert the song into the database
                db?.songsDao()?.insert(song)
            }
        } catch (e: Exception) {
            Log.d("PlayerActivity", "Error: $e")
            throw Exception("Error while fetching song")
        }
        return song
    }

    private suspend fun getTrack(trackId: String): Song {
        val authToken = getAuthToken()
        val networkService = getNetworkService()
        val track = networkService.getTrack(authToken, trackId)
        return track.toSong()
    }

    private fun bindSong() {
        with(binding) {
            songInfoNames.text = song?.title
            songInfoArtistMain.text = song?.artist
            songInfoGenreMain.text = song?.genres ?: "Unknown"
            /** Use glide to load the image */
            Glide.with(requireContext())
                .load(song?.coverPath)
                .into(songIcon)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "Saving state")
        // Save the state of the song
        outState.putString("trackId", trackId)
        outState.putSerializable("song", song)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}