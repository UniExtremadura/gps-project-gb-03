package com.unex.musicgo.ui.fragments

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.unex.musicgo.R
import com.unex.musicgo.databinding.SongDetailsFragmentBinding
import com.unex.musicgo.models.Song
import com.unex.musicgo.ui.vms.SongDetailsFragmentViewModel

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

    private val viewModel: SongDetailsFragmentViewModel by lazy {
        ViewModelProvider(
            this,
            SongDetailsFragmentViewModel.Factory
        )[SongDetailsFragmentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        arguments?.let {
            Log.d(TAG, "onCreate arguments: $it")
            val trackId = it.getString(EXTRA_TRACK_ID)
            viewModel.setTrackId(trackId!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        _binding = SongDetailsFragmentBinding.inflate(inflater, container, false)
        restoreState(savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        setUpViewModel()
    }

    private fun restoreState(savedInstanceState: Bundle?): Boolean {
        if (savedInstanceState == null) return false
        Log.d(TAG, "restoreState")
        Log.d(TAG, "restoreState savedInstanceState: $savedInstanceState")
        viewModel.restoreState(savedInstanceState)
        return true
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.trackId.observe(viewLifecycleOwner) {
            Log.d(TAG, "setUpViewModel trackId: $it")
            viewModel.load(requireContext()) {
                viewModel.fetchSong(it)
            }
        }
        viewModel.isPlaying.observe(viewLifecycleOwner) {
            if (it) {
                // Is playing
                binding.playButton.visibility = View.GONE
                binding.pauseButton.visibility = View.VISIBLE
            } else {
                // Is paused or stopped
                binding.playButton.visibility = View.VISIBLE
                binding.pauseButton.visibility = View.GONE
            }
        }
        viewModel.isStopped.observe(viewLifecycleOwner) {
            if (it) {
                // Is stopped
                binding.playButton.visibility = View.VISIBLE
                binding.pauseButton.visibility = View.GONE
            }
        }
        viewModel.song.observe(viewLifecycleOwner) {
            Log.d(TAG, "setUpViewModel song: $it")
            bind(it)
        }
        viewModel.progress.observe(viewLifecycleOwner) {
            Log.d(TAG, "setUpViewModel progress: $it")
            binding.songProgress.max = viewModel.maxProgress()
            binding.songProgress.progress = it.toInt()
        }
        viewModel.favoritesPlayListLiveData.observe(viewLifecycleOwner) {
            Log.d(TAG, "setUpViewModel favoritesPlayListLiveData: $it")
        }
    }

    private fun fillStars(stars: Int) {
        if (stars < 0 || stars > 5) return
        val starIds = listOf(
            R.id.star_1,
            R.id.star_2,
            R.id.star_3,
            R.id.star_4,
            R.id.star_5
        )
        for (element in starIds) {
            val star = binding.root.findViewById<View>(element)
            star.setBackgroundResource(R.drawable.ic_star)
        }
        for (i in 0 until stars) {
            val star = binding.root.findViewById<View>(starIds[i])
            star.setBackgroundResource(R.drawable.ic_star_filled)
        }
    }

    private fun addStarsListeners(block: (stars: Int) -> Unit) {
        val starIds = listOf(
            R.id.star_1,
            R.id.star_2,
            R.id.star_3,
            R.id.star_4,
            R.id.star_5
        )
        for (i in starIds.indices) {
            val star = binding.root.findViewById<View>(starIds[i])
            star.setOnClickListener {
                block(i + 1)
            }
        }
    }

    fun bind(song: Song) {
        binding.songInfoNames.text = song.title
        binding.songInfoArtistMain.text = song.artist
        binding.songInfoGenreMain.text = song.genres ?: "Unknown"
        song.isRated.let {
            if (it) {
                fillStars(song.rating)
            }
        }
        /** Use glide to load the image */
        Glide.with(requireContext())
            .load(song.coverPath)
            .into(binding.songIcon)
        /** Bind the comments */
        launchCommentFragment()
        /** Add listener to the rating stars icons */
        addStarsListeners { starsClicked ->
            fillStars(starsClicked)
            viewModel.updateRating(starsClicked)
        }
        /** Add listener to the play button */
        binding.playButton.setOnClickListener {
            viewModel.play()
        }
        /** Add listener to the pause button */
        binding.pauseButton.setOnClickListener {
            viewModel.pause()
        }
        /** Add listener to the stop button */
        binding.stopButton.setOnClickListener {
            viewModel.stop()
        }
        /** Add a checker to progress bar */
        binding.songProgress.max = 0
        binding.songProgress.progress = 0
        viewModel.startProgressChecker()
        /** Add listener to the seek bar */
        binding.songProgress.setOnSeekBarChangeListener(object :
            android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: android.widget.SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    viewModel.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
        /** Add listener to the new comment */
        binding.newCommentField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val commentText = binding.newCommentField.text.toString()
                viewModel.writingComment(commentText) {
                    binding.newCommentField.clearFocus()
                    binding.newCommentField.text.clear()
                    launchCommentFragment()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun launchCommentFragment() {
        val song = viewModel.song.value!!
        val fragment = CommentListFragment.newInstance(song)
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(binding.songInfoComments.id, fragment)
        transaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
        // Save the state of the view model
        viewModel.saveState(outState)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        // Pause the media player
        viewModel.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
        // Destroy the media player
        viewModel.destroyMediaPlayer()
        // Save the statistics
        viewModel.saveStatistics()
        // Avoid memory leaks
        _binding = null
    }

}