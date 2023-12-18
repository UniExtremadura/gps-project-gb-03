package com.unex.musicgo.ui.vms

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unex.musicgo.MusicGoApplication
import com.unex.musicgo.models.Comment
import com.unex.musicgo.models.PlayListSongCrossRef
import com.unex.musicgo.models.PlayListWithSongs
import com.unex.musicgo.models.Song
import com.unex.musicgo.utils.PlayListRepository
import com.unex.musicgo.utils.Repository
import com.unex.musicgo.utils.SongRepository
import com.unex.musicgo.utils.UserRepository
import kotlinx.coroutines.launch

class SongDetailsFragmentViewModel(
    private val userRepository: UserRepository,
    private val songRepository: SongRepository,
    private val playListRepository: PlayListRepository,
    private val repository: Repository
): ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)

    /* Song */
    private val _trackId = MutableLiveData("")
    val trackId: LiveData<String> = _trackId

    private val _song = MutableLiveData<Song>()
    val song: LiveData<Song> = _song

    /* Favorite playlist */
    private var _favoritesPlayList = playListRepository.favoritePlayList
    val favoritesPlayListLiveData: LiveData<PlayListWithSongs> = _favoritesPlayList
    val favoritesPlayList: PlayListWithSongs? get() = _favoritesPlayList.value

    /* Controls */
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying
    private val _isStopped = MutableLiveData(false)
    val isStopped: LiveData<Boolean> = _isStopped
    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    /* Statistics */
    private var timePlayed: Long = 0

    /* Media player */
    private var mediaPlayer: MediaPlayer? = null
    private var isMediaPlayerPrepared = false

    /* Progress bar checker */
    private val handler = Handler(Looper.getMainLooper())
    private var lastProgress = 0
    private val progressChecker: Runnable = object : Runnable {
        override fun run() {
            if (isMediaPlayerPrepared) {
                val currentProgress = mediaPlayer?.currentPosition
                if (currentProgress == null) {
                    handler.postDelayed(this, 1000)
                    return
                }
                if (currentProgress != lastProgress) {
                    lastProgress = currentProgress
                    _progress.postValue(currentProgress)

                    // Save statistics
                    timePlayed += 1000
                }
            }
            handler.postDelayed(this, 1000)
        }
    }

    /* Comments */
    private val _commentsHandler = Handler(Looper.getMainLooper())
    private val _commentDelayToPost = 3000L // 3 seconds

    fun setTrackId(trackId: String) {
        Log.d(TAG, "setTrackId: $trackId")
        if (trackId.isEmpty()) return
        _trackId.postValue(trackId)
    }

    fun destroyMediaPlayer() {
        stopProgressChecker()
        if (isMediaPlayerPrepared && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isMediaPlayerPrepared = false
        }
    }

    fun startProgressChecker() {
        handler.postDelayed(progressChecker, 1000)
    }

    private fun stopProgressChecker() {
        handler.removeCallbacks(progressChecker)
    }

    fun maxProgress(): Int {
        if (mediaPlayer == null) return 0
        if (!isMediaPlayerPrepared) return 0
        return mediaPlayer!!.duration
    }

    fun seekTo(progress: Int) {
        mediaPlayer?.seekTo(progress)
    }

    fun updateRating(stars: Int) {
        viewModelScope.launch {
            try {
                val song = song.value!!
                isLoading.postValue(true)
                Log.d(TAG, "updateRating: $song")
                // Update the rating of the song
                playListRepository.rateSong(_favoritesPlayList.value!!, song, stars)
            } catch (e: Exception) {
                toastLiveData.postValue(e.message)
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun play() {
        Log.d(TAG, "play")
        _isPlaying.postValue(true)
        _isStopped.postValue(false)
        mediaPlayer?.start()
    }

    fun pause() {
        Log.d(TAG, "pause")
        _isPlaying.postValue(false)
        _isStopped.postValue(false)
        mediaPlayer?.pause()
    }

    fun stop() {
        Log.d(TAG, "stop")
        _isPlaying.postValue(false)
        _isStopped.postValue(true)
        mediaPlayer?.pause()
        mediaPlayer?.seekTo(0)
    }

    suspend fun fetchSong(trackId: String) {
        songRepository.fetchSong(trackId) {
            _song.postValue(it)
            prepareMediaPlayer(it.previewUrl)
        }
    }

    private fun prepareMediaPlayer(previewUrl: String?, block: () -> Unit? = {}) {
        Log.d(TAG, "prepareMediaPlayer")
        previewUrl?.let { url ->
            Log.d(TAG, "prepareMediaPlayer url: $url")
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                this.setDataSource(url)
                setOnPreparedListener {
                    isMediaPlayerPrepared = true
                    block()
                }
                setOnCompletionListener {
                    _isPlaying.postValue(false)
                    _isStopped.postValue(true)
                }
                this.prepareAsync()
            }
        }
    }

    fun restoreState(bundle: Bundle?) {
        bundle?.let {
            Log.d(TAG, "restoreState bundle: $it")
            val trackId = it.getString("trackId")
            val song = it.getSerializable("song") as Song
            val progress: Long = it.getLong("progress")
            val isPlaying = it.getBoolean("isPlaying")
            val isStopped = it.getBoolean("isStopped")
            _trackId.postValue(trackId!!)
            _song.postValue(song)
            _isPlaying.postValue(isPlaying)
            _isStopped.postValue(isStopped)
            prepareMediaPlayer(song.previewUrl) {
                mediaPlayer?.seekTo(progress.toInt())
                if (isPlaying) {
                    play()
                }
            }
        }
    }

    fun saveState(outState: Bundle?) {
        outState?.let {
            it.putString("trackId", trackId.value)
            it.putSerializable("song", song.value)
            it.putLong("progress", mediaPlayer?.currentPosition?.toLong() ?: 0)
            it.putBoolean("isPlaying", isPlaying.value ?: false)
            it.putBoolean("isStopped", isStopped.value ?: false)
        }
    }

    fun writingComment(comment: String, success: () -> Unit = {}) {
        // Stop the previous operation of saving the text
        this._commentsHandler.removeCallbacksAndMessages(null)

        // Save the text after delay
        this._commentsHandler.postDelayed({
            saveComment(comment) {
                success()
            }
        }, _commentDelayToPost)
    }

    private fun saveComment(comment: String, success: () -> Unit = {}) {
        // Validate the comment
        if (comment.isEmpty()) {
            toastLiveData.postValue("Comment is empty")
            return
        }
        // Validate the user
        val email = repository.auth.currentUser?.email
        if (email == null) {
            toastLiveData.postValue("You must be logged in to comment")
            return
        }
        // Save the comment
        viewModelScope.launch {
            val user = userRepository.currentUser.value
            if (user == null) {
                toastLiveData.postValue("You must be logged in to comment")
                return@launch
            }
            val username = user.username
            username.let {
                val commentRef = Firebase.firestore.collection("comments").document()
                val commentObj = Comment(
                    songId = song.value!!.id,
                    authorEmail = email,
                    username = it,
                    description = comment,
                    timestamp = System.currentTimeMillis()
                )
                commentRef.set(commentObj)
                toastLiveData.postValue("Comment posted")
                success()
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork == null) toastLiveData.postValue("No internet connection")
        return activeNetwork != null
    }

    fun load(context: Context, block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                if(isNetworkAvailable(context)) {
                    block()
                }
            } catch (e: Exception) {
                toastLiveData.postValue(e.message)
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun saveStatistics() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "saveStatistics: ${song.value!!.id}, timePlayed: $timePlayed")
                isLoading.postValue(true)
                repository.database.statisticsDao().registerPlay(
                    song.value!!.id,
                    song.value!!.title,
                    song.value!!.artist,
                    timePlayed
                )
                Log.d(TAG, "saveStatistics success")
            } catch (e: Exception) {
                toastLiveData.postValue(e.message)
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    companion object {
        const val TAG = "SongDetailsFragmentViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val app = application as MusicGoApplication
                val viewModel = SongDetailsFragmentViewModel(
                    app.appContainer.userRepository,
                    app.appContainer.songRepository,
                    app.appContainer.playListRepository,
                    app.appContainer.repository,
                )
                return viewModel as T
            }
        }
    }
}