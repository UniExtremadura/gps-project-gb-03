package com.unex.musicgo.ui.vms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unex.musicgo.api.getAuthToken
import com.unex.musicgo.api.getNetworkService
import com.unex.musicgo.models.Genre
import kotlinx.coroutines.launch

class GenreListFragmentViewModel: ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val spinnerActiveLiveData = MutableLiveData<Boolean>()

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> get() = _genres

    fun fetchGenres() {
        viewModelScope.launch {
            try {
                spinnerActiveLiveData.postValue(true)
                val networkService = getNetworkService()
                val authToken = getAuthToken()
                val response = networkService.getAvailableGenres(authToken)
                val genres = response.genres.map {
                    Genre(it)
                }
                _genres.postValue(genres)
            } catch (e: Exception) {
                toastLiveData.postValue(e.message)
            } finally {
                spinnerActiveLiveData.postValue(false)
            }
        }
    }
}