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
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.models.Comment
import com.unex.musicgo.ui.adapters.CommentListAdapter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CommentListFragment : Fragment() {

    private val TAG = "CommentListFragment"
    private var _comments: List<Comment> = emptyList()

    private var binding: SongListFragmentBinding? = null
    private lateinit var adapter: CommentListAdapter

    private var db: MusicGoDatabase? = null
    private lateinit var song: Song

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate CommentListFragment")

        arguments?.let {
            song = it.getSerializable(ARG_SONG) as Song
        }

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
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
        setUpRecyclerView()
        lifecycleScope.launch {
            binding?.let {
                it.spinner.visibility = View.VISIBLE
                try {
                    _comments = sortCommentsByTimestamp(fetchComments())
                    adapter.updateData(_comments)
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

    private fun sortCommentsByTimestamp(comments: List<Comment>) =
        comments.sortedByDescending { it.timestamp }

    private suspend fun fetchComments(): List<Comment> = suspendCoroutine { continuation ->
        // Get the comments for the given song id
        if (!isNetworkAvailable()) {
            continuation.resumeWithException(Error("No internet connection"))
        }
        if (Firebase.auth.currentUser == null) {
            continuation.resumeWithException(Error("User not logged in"))
        }
        val collectionRef = Firebase.firestore.collection("comments")
        val query = collectionRef.whereEqualTo("songId", song.id)
        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "Comments fetched successfully")
                val comments = mutableListOf<Comment>()
                for (document in it.result!!) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val songId = document.data["songId"].toString()
                    val authorEmail = document.data["authorEmail"].toString()
                    val username = document.data["username"].toString()
                    val description = document.data["description"].toString()
                    val timestamp = document.data["timestamp"].toString().toLong()
                    comments.add(
                        Comment(
                            songId = songId,
                            authorEmail = authorEmail,
                            username = username,
                            description = description,
                            timestamp = timestamp
                        )
                    )
                }
                Log.d(TAG, "Returning comments...: $comments")
                continuation.resume(comments)
            } else {
                Log.d(TAG, "Error getting documents: ", it.exception)
                continuation.resumeWithException(Error("Error getting comments"))
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

    private fun setUpRecyclerView() {
        binding?.let {
            adapter = CommentListAdapter(
                comments = _comments,
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