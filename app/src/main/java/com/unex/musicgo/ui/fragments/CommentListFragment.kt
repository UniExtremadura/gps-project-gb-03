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
import com.unex.musicgo.models.Song
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.unex.musicgo.ui.adapters.CommentListAdapter
import com.unex.musicgo.ui.vms.CommentListFragmentViewModel

class CommentListFragment : Fragment() {

    private val TAG = "CommentListFragment"

    private var binding: SongListFragmentBinding? = null
    private lateinit var adapter: CommentListAdapter

    private val viewModel: CommentListFragmentViewModel by lazy {
        ViewModelProvider(this)[CommentListFragmentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate CommentListFragment")

        arguments?.let {
            val song = it.getSerializable(ARG_SONG) as Song
            viewModel.setSong(song)
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach CommentListFragment")
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView CommentListFragment")
        binding = SongListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
        setUpRecyclerView()
        lifecycleScope.launch {
            if (!viewModel.isNetworkAvailable(requireContext())) {
                viewModel.toastLiveData.value = "No internet connection"
                return@launch
            }
            viewModel.fetchComments()
        }
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.spinnerActivated.observe(viewLifecycleOwner) {
            binding?.spinner?.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.sortedComments.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    private fun setUpRecyclerView() {
        binding?.let {
            adapter = CommentListAdapter(
                comments = emptyList(),
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
        private const val ARG_SONG = "song"

        @JvmStatic
        fun newInstance(song: Song): CommentListFragment {
            return CommentListFragment()
                .apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_SONG, song)
                    }
                }
        }
    }
}