package com.unex.musicgo.ui.vms

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.unex.musicgo.MusicGoApplication
import com.unex.musicgo.models.PlayListWithSongs
import com.unex.musicgo.models.Song
import com.unex.musicgo.ui.enums.SongListFragmentOption
import com.unex.musicgo.utils.PlayListRepository
import com.unex.musicgo.utils.SongRepository

class SongListFragmentViewModel(
    private val playListRepository: PlayListRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    val toastLiveData = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)

    /* Option */
    private var _option = MutableLiveData<String>()
    val option: LiveData<String> = _option

    /* Search */
    private var _query = MutableLiveData<String>()
    val query: LiveData<String> = _query

    /* Favorites */
    private var _stars = MutableLiveData<Int?>()
    private val stars: LiveData<Int?> = _stars

    /* PlayList */
    private var _playlist = MutableLiveData<PlayListWithSongs?>()
    private val playlist: LiveData<PlayListWithSongs?> = _playlist

    /* Recommendations */
    private var _genreSeed = MutableLiveData<String>()
    private val genreSeed: LiveData<String> = _genreSeed
    private var _artistSeed = MutableLiveData<String>()
    private val artistSeed: LiveData<String> = _artistSeed

    /* Songs data */
    private var _songs = MutableLiveData<List<Song>>()
    private var _songsFromPlayList = playListRepository.songs
    val songs: LiveData<List<Song>> = MediatorLiveData()

    /* Fetch trigger */
    private val _fetchTrigger = MediatorLiveData<Unit>()
    val canFetch: LiveData<Unit> = _fetchTrigger

    init {
        (songs as MediatorLiveData).addSource(_songs) {
            songs.postValue(it)
        }
        songs.addSource(_songsFromPlayList) {
            songs.postValue(it)
        }
        _fetchTrigger.addSource(_option) {
            checkToFetchData()
        }
        _fetchTrigger.addSource(_query) {
            checkToFetchData()
        }
        _fetchTrigger.addSource(_stars) {
            checkToFetchData()
        }
        _fetchTrigger.addSource(_genreSeed) {
            checkToFetchData()
        }
        _fetchTrigger.addSource(_artistSeed) {
            checkToFetchData()
        }
        _fetchTrigger.addSource(_playlist) {
            checkToFetchData()
        }
    }

    private fun checkToFetchData() {
        val optionIsNullOrEmpty = option.value.isNullOrEmpty()
        if (optionIsNullOrEmpty) return
        when (SongListFragmentOption.valueOf(option.value!!).name) {
            SongListFragmentOption.RECENT.name -> {
                _fetchTrigger.postValue(Unit)
            }
            SongListFragmentOption.SEARCH.name -> {
                val queryIsNullOrEmpty = query.value.isNullOrEmpty()
                if (!queryIsNullOrEmpty) {
                    _fetchTrigger.postValue(Unit)
                }
            }
            SongListFragmentOption.FAVORITES.name -> {
                val starsNotDefined = stars.value == null
                if (!starsNotDefined) {
                    _fetchTrigger.postValue(Unit)
                }
            }
            SongListFragmentOption.RECOMMENDATION.name -> {
                _fetchTrigger.postValue(Unit)
            }
            SongListFragmentOption.PLAYLIST.name -> {
                _fetchTrigger.postValue(Unit)
            }
        }
    }

    fun setOption(option: SongListFragmentOption) {
        _option.postValue(option.name)
    }

    fun showTrash() = _option.value == SongListFragmentOption.PLAYLIST.name

    suspend fun fetch() {
        when (option.value) {
            SongListFragmentOption.RECENT.name -> {
                this.loadWithOutNetwork {
                    playListRepository.fetchRecentSongs()
                }
            }
            SongListFragmentOption.SEARCH.name -> {
                songRepository.fetchSearch(
                    query = query.value ?: "",
                    onSuccess = { songs ->
                        _songs.postValue(songs)
                    },
                    onError = { message ->
                        toastLiveData.postValue(message)
                    }
                )
            }
            SongListFragmentOption.FAVORITES.name -> playListRepository.fetchFavoritesSongs(stars.value)
            SongListFragmentOption.RECOMMENDATION.name -> songRepository.fetchRecommendations(
                artistSeed = artistSeed.value,
                genreSeed = genreSeed.value,
                onSuccess = { songs ->
                    _songs.postValue(songs)
                },
                onError = { message ->
                    toastLiveData.postValue(message)
                }
            )
            SongListFragmentOption.PLAYLIST.name -> fetchPlayList()
            else -> throw Exception("Option not found")
        }
    }

    private fun fetchPlayList() {
        val songs = playlist.value?.songs ?: throw Exception("Playlist not found")
        _songs.postValue(songs)
    }

    fun setRecommendationSeeds(genre: String?, artist: String?) {
        _genreSeed.postValue(genre ?: "")
        _artistSeed.postValue(artist ?: "")
    }

    fun setQuery(query: String?) {
        query?.let {
            Log.d("SongListFragmentViewModel", "setQuery: $it")
            _query.postValue(it)
        }
    }

    fun setPlayList(playList: PlayListWithSongs?) {
        if(playList == null) return
        if(playList == _playlist.value) return
        _playlist.postValue(playList)
    }

    fun setStars(stars: Int?) {
        if (stars == null) return
        if (stars == _stars.value) return
        _stars.postValue(stars)
    }

    private fun loadWithOutNetwork(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                block()
            } catch (e: Exception) {
                toastLiveData.postValue(e.message)
            } finally {
                isLoading.postValue(false)
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

    companion object {
        const val TAG = "SongListFragmentViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val app = application as MusicGoApplication
                val viewModel = SongListFragmentViewModel(
                    app.appContainer.playListRepository,
                    app.appContainer.songRepository
                )
                return viewModel as T
            }
        }
    }
}