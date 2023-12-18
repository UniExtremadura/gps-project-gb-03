package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.unex.musicgo.databinding.DiscoverFragmentBinding
import com.unex.musicgo.models.Genre
import com.unex.musicgo.ui.interfaces.OnSongClickListener
import com.unex.musicgo.ui.vms.DiscoverFragmentViewModel
import kotlinx.coroutines.launch

class DiscoverFragment : Fragment(), GenreListFragment.OnGenreClickListener {

    private val TAG = "DiscoverFragment"

    companion object {
        fun newInstance() = DiscoverFragment()
    }

    private var _binding: DiscoverFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var onSongClickListener: OnSongClickListener

    private val viewModel: DiscoverFragmentViewModel by lazy {
        ViewModelProvider(this)[DiscoverFragmentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate DiscoverFragment")
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
            replaceRecommendationsFragment()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewModel()

        with(binding) {
            this.btnApplyFilters.setOnClickListener {
                lifecycleScope.launch {
                    if (viewModel.isNetworkAvailable(requireContext())) {
                        viewModel.fetchArtistsSeed(searchBar.query.toString())
                    }
                }
            }
        }
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.artist.observe(viewLifecycleOwner) {
            replaceRecommendationsFragment()
        }
    }

    private fun replaceRecommendationsFragment() {
        val fragment = SongListFragment.newRecommendationsInstance(viewModel.formattedGenres.value, viewModel.artist.value)
        childFragmentManager.beginTransaction()
            .replace(binding.fragmentSongListContainer.id, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

    override fun onGenreClick(genre: Genre) = viewModel.clickedOnGenre(genre)

}