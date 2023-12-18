package com.unex.musicgo.ui.vms

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.gson.Gson
import com.unex.musicgo.MusicGoApplication
import com.unex.musicgo.models.PlayList
import com.unex.musicgo.models.PlayListWithSongs
import com.unex.musicgo.models.Song
import com.unex.musicgo.ui.enums.PlayListDetailsFragmentOption
import com.unex.musicgo.utils.PlayListRepository
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class PlayListDetailsFragmentViewModel(
    private val playListRepository: PlayListRepository
): ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)

    /** State of the fragment: View, Edit, Create */
    private var state: PlayListDetailsFragmentOption = PlayListDetailsFragmentOption.VIEW

    /** PlayList data */
    private val _playList: MutableLiveData<PlayListWithSongs> = MutableLiveData()
    val playList: LiveData<PlayListWithSongs> = _playList
    private val _newPlayList: MutableLiveData<PlayList> = MutableLiveData()

    /** Playlists */
    private var playlists = playListRepository.getAllUserPlayLists()

    /** Brush active */
    var isTitleBrushActive = false
    var isDescriptionBrushActive = false

    /** Next and previous arrows */
    val nextArrowVisibility: LiveData<Boolean> = _playList.switchMap {
        val mutableLiveData = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val playlists = playlists.value ?: return@launch
            val playlist = it.playlist
            val index = playlists.indexOf(playlist)
            mutableLiveData.postValue(index < playlists.size - 1)
        }
        mutableLiveData
    }
    val previousArrowVisibility: LiveData<Boolean> = _playList.switchMap {
        val mutableLiveData = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val playlists = playlists.value ?: return@launch
            val playlist = it.playlist
            val index = playlists.indexOf(playlist)
            mutableLiveData.postValue(index > 0)
        }
        mutableLiveData
    }

    fun setStartMode(mode: PlayListDetailsFragmentOption, playList: PlayList?) {
        state = mode
        setPlayList(playList)
    }

    private fun setPlayList(playList: PlayList?) {
        playList?.let {
            viewModelScope.launch {
                playListRepository.fetchPlayList(it.id) {
                    _playList.postValue(it)
                }
            }
        }
    }

    fun previousPlayList() {
        val playlists = playlists.value ?: return
        val playlist = playList.value?.playlist ?: return
        val index = playlists.indexOf(playlist)
        if (index > 0) {
            setPlayList(playlists[index - 1])
        }
    }

    fun nextPlayList() {
        val playlists = playlists.value ?: return
        val playlist = playList.value?.playlist ?: return
        val index = playlists.indexOf(playlist)
        if (index < playlists.size - 1) {
            setPlayList(playlists[index + 1])
        }
    }

    fun hideTrashIcon(): Boolean = state == PlayListDetailsFragmentOption.CREATE
    fun hideShareIcon(): Boolean = state == PlayListDetailsFragmentOption.CREATE

    fun toggleTitleBrushActive() {
        isTitleBrushActive = !isTitleBrushActive
    }

    fun toggleDescriptionBrushActive() {
        isDescriptionBrushActive = !isDescriptionBrushActive
    }

    fun validTitle(text: String): Boolean {
        if (text.isNotEmpty()) {
            return true
        }
        toastLiveData.value = "The title of the playlist cannot be empty"
        return false
    }

    fun validDescription(text: String): Boolean {
        if (text.isNotEmpty()) {
            return true
        }
        toastLiveData.value = "The description of the playlist cannot be empty"
        return false
    }

    fun onTitleBrush(onVisible: () -> Unit? = {}, onInvisible: () -> Unit? = {}) {
        if (isTitleBrushActive) {
            onVisible()
        } else {
            onInvisible()
        }
    }

    fun onDescriptionBrush(onVisible: () -> Unit? = {}, onInvisible: () -> Unit? = {}) {
        if (isDescriptionBrushActive) {
            onVisible()
        } else {
            onInvisible()
        }
    }

    fun saveTitle(text: String, onSuccess: () -> Unit = {}) {
        if (isCreating()) {
            // Creando una nueva playlist
            if (_newPlayList.value == null) {
                _newPlayList.postValue(PlayList(title = text, description = ""))
                toastLiveData.postValue("Add a description to the playlist to save it")
            } else {
                val playlist = _newPlayList.value!!
                playlist.title = text
                _newPlayList.postValue(playlist)
                viewModelScope.launch {
                    if (state == PlayListDetailsFragmentOption.CREATE) {
                        playListRepository.insertPlayList(playlist)
                    } else {
                        playListRepository.updatePlayList(playlist)
                    }
                }
                onSuccess()
            }
        } else {
            // Editando una playlist
            val playlist = _playList.value?.playlist ?: return
            playlist.title = text
            _playList.postValue(_playList.value)
            viewModelScope.launch {
                playListRepository.updatePlayList(playlist)
            }
            onSuccess()
        }
    }

    fun saveDescription(text: String, onSuccess: () -> Unit = {}) {
        if (isCreating()) {
            // Creando una nueva playlist
            if (_newPlayList.value == null) {
                _newPlayList.postValue(PlayList(title = "", description = text))
                toastLiveData.postValue("Add a title to the playlist to save it")
            } else {
                val playlist = _newPlayList.value!!
                playlist.description = text
                _newPlayList.postValue(playlist)
                viewModelScope.launch {
                    if (state == PlayListDetailsFragmentOption.CREATE) {
                        playListRepository.insertPlayList(playlist)
                    } else {
                        playListRepository.updatePlayList(playlist)
                    }
                }
                onSuccess()
            }
        } else {
            // Editando una playlist
            val playlist = _playList.value?.playlist ?: return
            playlist.description = text
            _playList.postValue(_playList.value)
            viewModelScope.launch {
                playListRepository.updatePlayList(playlist)
            }
            onSuccess()
        }
    }

    fun deletePlayList(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val playlist = _playList.value?.playlist ?: return@launch
            playListRepository.deletePlayList(playlist)
            onSuccess()
        }
    }

    fun getRedirectUrl(): String {
        val playlist = _playList.value?.playlist ?: throw IllegalStateException("Playlist is null")
        val gson = Gson()
        val playlistJson = gson.toJson(playlist)
        val encodedJson = Base64.encodeToString(
            playlistJson.toByteArray(StandardCharsets.UTF_8),
            Base64.NO_WRAP
        )
        val uri = "musicgo://playlist?data=$encodedJson"
        val redirectUrl =
            "655a04d83b89142e66e542c0--flourishing-cajeta-2f3342.netlify.app/musicgo/redirect?url=$uri"
        return "Check out this playlist: $redirectUrl"
    }

    fun deleteSongFromPlayList(song: Song, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val title = song.title
            val playlist = _playList.value?.playlist ?: return@launch
            playListRepository.deleteSongFromPlayList(song, playlist)
            playListRepository.fetchPlayList(playlist.id) {
                _playList.postValue(it)
            }
            toastLiveData.postValue("Song \"$title\" deleted from playlist")
            onSuccess()
        }
    }

    fun isCreating(): Boolean = state == PlayListDetailsFragmentOption.CREATE

    companion object {
        const val TAG = "PlayListDetailsFragmentViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val app = application as MusicGoApplication
                val viewModel = PlayListDetailsFragmentViewModel(
                    app.appContainer.playListRepository
                )
                return viewModel as T
            }
        }
    }
}