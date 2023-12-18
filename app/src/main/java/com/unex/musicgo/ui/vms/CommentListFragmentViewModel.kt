package com.unex.musicgo.ui.vms

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unex.musicgo.models.Comment
import com.unex.musicgo.models.Song
import kotlinx.coroutines.tasks.await

class CommentListFragmentViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
): ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val spinnerActivated = MutableLiveData<Boolean>()

    private val _song = MutableLiveData<Song>()
    val song: LiveData<Song> get() = _song
    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments
    val sortedComments: LiveData<List<Comment>> = comments.switchMap { comments ->
        MutableLiveData(comments.sortedByDescending { it.timestamp })
    }

    fun setSong(song: Song) {
        _song.value = song
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: Network? = connectivityManager.activeNetwork
        return activeNetwork != null
    }

    suspend fun fetchComments() {
        try {
            spinnerActivated.value = true
            if (auth.currentUser == null) throw Error("User not logged in")
            val collectionRef = Firebase.firestore.collection("comments")
            val query = collectionRef.whereEqualTo("songId", song.value?.id)
            val results = query.get().await()
            val comments = mutableListOf<Comment>()
            for (document in results) {
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
            _comments.value = comments
        } catch (e: Exception) {
            toastLiveData.value = "Error fetching comments: ${e.message}"
        } finally {
            spinnerActivated.value = false
        }
    }

}