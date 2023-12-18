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
import com.unex.musicgo.models.Genre
import com.unex.musicgo.ui.adapters.GenreListAdapter
import com.unex.musicgo.ui.vms.GenreListFragmentViewModel

class GenreListFragment : Fragment() {

    private val TAG = "GenreListFragment"

    private lateinit var listener: OnGenreClickListener

    interface OnGenreClickListener {
        fun onGenreClick(genre: Genre)
    }

    private var binding: SongListFragmentBinding? = null
    private lateinit var adapter: GenreListAdapter

    private val viewModel: GenreListFragmentViewModel by lazy {
        ViewModelProvider(this)[GenreListFragmentViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach GenreListFragment")
        super.onAttach(context)
        listener = if (parentFragment is OnGenreClickListener) {
            parentFragment as OnGenreClickListener
        } else if (context is OnGenreClickListener) {
            context
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

        setUpViewModel()
        setUpRecyclerView()

        viewModel.fetchGenres()
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.spinnerActiveLiveData.observe(viewLifecycleOwner) {
            binding?.spinner?.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.genres.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    private fun setUpRecyclerView() {
        binding?.let {
            adapter = GenreListAdapter(
                genres = emptyList(),
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