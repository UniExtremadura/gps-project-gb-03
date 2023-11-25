package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.StatisticsFragmentBinding
import com.unex.musicgo.models.UserStatistics
import kotlinx.coroutines.launch

class StatisticsFragment : Fragment() {

    companion object {
        const val TAG = "StatisticsFragment"

        fun newInstance() = StatisticsFragment()
    }

    private var _binding: StatisticsFragmentBinding? = null
    private val binding get() = _binding!!

    private var db: MusicGoDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate statisticsFragment")

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach SongListFragment")
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView SongListFragment")
        _binding = StatisticsFragmentBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            with(binding) {
                // Get the statistics from the database
                val songStatistics: UserStatistics? = db?.statisticsDao()?.getMostPlayedSong()
                val artistStatistics: UserStatistics? = db?.statisticsDao()?.getMostPlayedArtist()
                val totalTime: Long? = db?.statisticsDao()?.getTotalTimePlayed()
                // Set the statistics in the view
                favSongName.text = songStatistics?.title
                favSongArtist.text = songStatistics?.artist
                favArtistName.text = artistStatistics?.artist
                minutesListened.text = transformTime(totalTime ?: 0)
            }
        }
        return binding.root
    }

    private fun transformTime(time: Long): String {
        val days = time / 86400000
        val hours = (time % 86400000) / 3600000
        val minutes = (time % 3600000) / 60000
        val seconds = (time % 60000) / 1000
        return if (days > 0) {
            "$days days, $hours hours, $minutes minutes and $seconds seconds"
        } else if (hours > 0) {
            "$hours hours, $minutes minutes and $seconds seconds"
        } else if (minutes > 0) {
            "$minutes minutes and $seconds seconds"
        } else {
            "$seconds seconds"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}