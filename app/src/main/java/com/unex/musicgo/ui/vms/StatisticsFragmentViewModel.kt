package com.unex.musicgo.ui.vms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.unex.musicgo.MusicGoApplication
import com.unex.musicgo.utils.StatisticsRepository

class StatisticsFragmentViewModel(
    statisticsRepository: StatisticsRepository
): ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)

    val favoriteSongTitle: LiveData<String> = statisticsRepository.songStatistics
    val favoriteSongArtist: LiveData<String> = statisticsRepository.songArtistStatistics
    val favoriteArtistName: LiveData<String> = statisticsRepository.artistStatistics
    val totalTimePlayed: LiveData<String> = statisticsRepository.timeStatistics

    companion object {
        const val TAG = "StatisticsFragmentViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val app = application as MusicGoApplication
                val viewModel = StatisticsFragmentViewModel(
                    app.appContainer.statisticsRepository
                )
                return viewModel as T
            }
        }
    }
}