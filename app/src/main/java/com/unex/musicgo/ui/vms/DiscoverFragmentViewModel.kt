package com.unex.musicgo.ui.vms

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.unex.musicgo.api.getAuthToken
import com.unex.musicgo.api.getNetworkService
import com.unex.musicgo.data.api.search.SearchResponse
import com.unex.musicgo.models.Genre

class DiscoverFragmentViewModel: ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val spinnerActivated = MutableLiveData<Boolean>()

    private val _genres = MutableLiveData<List<String>>()
    val genres: LiveData<List<String>> = _genres
    val formattedGenres: LiveData<String> = genres.switchMap {
        MutableLiveData(it.joinToString(separator = "%2C"))
    }

    private val _artist = MutableLiveData<String>()
    val artist: LiveData<String> = _artist

    init {
        _genres.value = listOf()
        _artist.value = ""
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network: Network? = connectivityManager.activeNetwork
        if (network == null) toastLiveData.value = "No internet connection"
        return network != null
    }

    fun clickedOnGenre(genre: Genre) {
        if (genres.value?.contains(genre.title) == true) {
            _genres.value = genres.value?.minus(genre.title)
        } else {
            _genres.value = genres.value?.plus(genre.title)
        }
    }

    suspend fun fetchArtistsSeed(artist: String? = "") {
        try {
            spinnerActivated.value = true
            _artist.value = if (artist.isNullOrEmpty()) {
                ""
            } else {
                val service = getNetworkService()
                val auth = getAuthToken()
                val response: SearchResponse =
                    service.search(auth = auth, query = artist, type = "artist", limit = 1)
                val artistId = response.artists?.items?.get(0)?.id
                artistId ?: ""
            }
        } catch (e: Exception) {
            toastLiveData.value = "Error fetching artists seed"
        } finally {
            spinnerActivated.value = false
        }
    }

}