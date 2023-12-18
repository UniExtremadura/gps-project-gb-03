package com.unex.musicgo.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.unex.musicgo.databinding.StatisticsFragmentBinding
import com.unex.musicgo.ui.vms.StatisticsFragmentViewModel

class StatisticsFragment : Fragment() {

    companion object {
        const val TAG = "StatisticsFragment"

        fun newInstance() = StatisticsFragment()
    }

    private var _binding: StatisticsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsFragmentViewModel by lazy {
        ViewModelProvider(
            this,
            StatisticsFragmentViewModel.Factory
        )[StatisticsFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView SongListFragment")
        _binding = StatisticsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewModel()
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.favoriteSongTitle.observe(viewLifecycleOwner) {
            binding.favSongName.text = it
        }
        viewModel.favoriteSongArtist.observe(viewLifecycleOwner) {
            binding.favSongArtist.text = it
        }
        viewModel.favoriteArtistName.observe(viewLifecycleOwner) {
            binding.favArtistName.text = it
        }
        viewModel.totalTimePlayed.observe(viewLifecycleOwner) {
            binding.minutesListened.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}