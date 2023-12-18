package com.unex.musicgo.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.unex.musicgo.database.dao.PlayListDao
import com.unex.musicgo.database.dao.PlayListSongCrossRefDao
import com.unex.musicgo.database.dao.SongsDao
import com.unex.musicgo.models.PlayList
import com.unex.musicgo.models.PlayListSongCrossRef
import com.unex.musicgo.models.PlayListWithSongs
import com.unex.musicgo.models.Song

class PlayListRepository(
    private val songsDao: SongsDao,
    private val playListDao: PlayListDao,
    private val playListSongCrossRefDao: PlayListSongCrossRefDao
) {

    private var _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    val favoritePlayList = playListDao.getFavoritesPlayList()

    fun getAllUserPlayLists() = playListDao.getPlayListsCreatedByUserWithoutSongs()

    fun fetchRecentSongs() {
        playListDao.getRecentPlayList().observeForever {
            it?.let {
                _songs.postValue(it.songs)
            }
        }
    }

    fun fetchFavoritesSongs(stars: Int? = null) {
        playListDao.getFavoritesPlayList().observeForever {
            it?.let {
                var songs = it.songs
                if (stars != null) {
                    Log.d("PlayListRepository", "fetchFavoritesSongs: $stars")
                    if (stars != 0) {
                        songs = songs.filter {
                            it.rating == stars
                        }
                    }
                }
                _songs.postValue(songs)
            }
        }
    }

    fun fetchPlayList(
        playListId: Int,
        onSuccess: (PlayListWithSongs) -> Unit = {},
    ) {
        playListDao.getPlayList(playListId).observeForever {
            it?.let {
                onSuccess(it)
            }
        }
    }

    suspend fun insertPlayList(playList: PlayList) {
        playListDao.insert(playList)
    }

    suspend fun updatePlayList(playList: PlayList) {
        playListDao.update(playList)
    }

    suspend fun deletePlayList(playList: PlayList) {
        // Delete all the cross references between the playlist and the songs
        playListSongCrossRefDao.deleteAllByPlayList(playList.id)
        // Delete the playlist
        playListDao.delete(playList.id)
    }

    suspend fun deleteSongFromPlayList(song: Song, playList: PlayList) {
        playListSongCrossRefDao.delete(playList.id, song.id)
    }

    suspend fun rateSong(playList: PlayListWithSongs, song: Song, rating: Int) {
        Log.d("PlayListRepository", "rateSong: $rating")
        song.isRated = true
        song.rating = rating
        songsDao.insert(song)
        val songInFavorites = playList.songs.find { songInList ->
            songInList.id == song.id
        } != null
        if (songInFavorites && rating < 4) {
            playListSongCrossRefDao.delete(playList.playlist.id, song.id)
        } else if (rating >= 4) {
            if (!songInFavorites) {
                playListSongCrossRefDao.insert(PlayListSongCrossRef(playList.playlist.id, song.id))
            }
        }
    }
}