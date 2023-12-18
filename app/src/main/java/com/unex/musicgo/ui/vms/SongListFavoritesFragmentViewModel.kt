package com.unex.musicgo.ui.vms

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.unex.musicgo.MusicGoApplication
import com.unex.musicgo.models.Song
import com.unex.musicgo.utils.SongRepository
import com.unex.musicgo.utils.StatisticsRepository
import kotlinx.coroutines.launch

class SongListFavoritesFragmentViewModel(
    private val songRepository: SongRepository,
    private val statisticsRepository: StatisticsRepository
): ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)

    val listMostPlayedStatistics = statisticsRepository.mostPlayedSongs
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    fun fetchMostPlayedSongs() = statisticsRepository.fetchMostPlayedSong(3)

    suspend fun fetchSongs() {
        val listMostPlayedStatistics = statisticsRepository.mostPlayedSongs.value
        if (listMostPlayedStatistics == null) {
            toastLiveData.postValue("No songs found")
            return
        }
        val songs = mutableListOf<Song>()
        listMostPlayedStatistics.forEach { userStatistics ->
            songRepository.fetchSong(userStatistics.songId) {
                songs += it
            }
        }
        _songs.postValue(songs)
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

    companion object {
        const val TAG = "SongListFavoritesFragmentViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val app = application as MusicGoApplication
                val viewModel = SongListFavoritesFragmentViewModel(
                    app.appContainer.songRepository,
                    app.appContainer.statisticsRepository
                )
                return viewModel as T
            }
        }
    }
}