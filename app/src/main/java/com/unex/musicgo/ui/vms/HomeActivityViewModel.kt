package com.unex.musicgo.ui.vms

import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.gson.Gson
import com.unex.musicgo.MusicGoApplication
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.models.PlayList
import com.unex.musicgo.models.PlayListSongCrossRef
import com.unex.musicgo.models.PlayListWithSongs
import com.unex.musicgo.ui.activities.HomeActivity
import com.unex.musicgo.utils.Repository
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class HomeActivityViewModel(
    private val repository: Repository
): ViewModel() {

    val database by lazy {
        repository.database
    }

    val toastLiveData = MutableLiveData<String>()
    val confirmImportPlayListLiveData = MutableLiveData<PlayListWithSongs?>()

    suspend fun handlerUriData(uriData: Uri?) {
        if (uriData != null) {
            try {
                val encodedJson = uriData.getQueryParameter("data")
                val json =
                    String(Base64.decode(encodedJson, Base64.NO_WRAP), StandardCharsets.UTF_8)
                val gson = Gson()
                val playlist = gson.fromJson(json, PlayListWithSongs::class.java)
                // Check if the playlist already exists in the database
                val databasePlayList = database.playListDao().getPlayList(playlist.playlist.title, playlist.playlist.description)
                val playListExists = databasePlayList != null
                // If the playlist already exists, show a toast and return
                if (playListExists) {
                    toastLiveData.value = "Playlist already exists"
                    return
                }
                // Notify to show a dialog to confirm the import
                confirmImportPlayListLiveData.value = playlist
            } catch (e: Exception) {
                Log.e(HomeActivity.TAG, "Error parsing json", e)
                toastLiveData.value = "Error getting playlist"
            }
        }
    }

    suspend fun importPlayList(playlist: PlayListWithSongs) = viewModelScope.launch {
        // Create the new PlayList
        val newPlayList = PlayList(
            title = playlist.playlist.title,
            description = playlist.playlist.description,
            createdByUser = true
        )
        // Insert the new PlayList to the database
        val newPlayListId = database.playListDao().insert(newPlayList)
        // Create all the cross references between the new PlayList and the songs
        val crossRefs = playlist.songs.map { song ->
            PlayListSongCrossRef(newPlayListId.toInt(), song.id)
        }
        // Insert all the cross references to the database
        database.playListSongCrossRefDao().insertAll(crossRefs)
        // Insert all the songs to the database
        database.songsDao().insertAll(playlist.songs)
        // Show a error
        toastLiveData.value = "Playlist imported"
    }

    fun clearCache() = viewModelScope.launch {
        try {
            database.songsDao().clearCache()
        } catch (e: Exception) {
            Log.e(HomeActivity.TAG, "Error deleting old cache", e)
            toastLiveData.value = "Error deleting old cache"
        }
    }

    companion object {
        const val TAG = "HomeActivityViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val viewModel = HomeActivityViewModel(
                    (application as MusicGoApplication).appContainer.repository,
                )
                return viewModel as T
            }
        }
    }

}