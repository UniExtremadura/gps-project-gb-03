package com.unex.musicgo.utils

import android.util.Log
import com.unex.musicgo.api.MusicGoAPI
import com.unex.musicgo.api.getAuthToken
import com.unex.musicgo.data.api.common.Items
import com.unex.musicgo.data.toSong
import com.unex.musicgo.database.dao.SongsDao
import com.unex.musicgo.models.Song
import com.unex.musicgo.ui.vms.SongDetailsFragmentViewModel

class SongRepository(
    private val networkService: MusicGoAPI,
    private val songsDao: SongsDao
) {

    private val TAG = "SongRepository"

    /**
     * Fetch a song from the database or the network with the given trackId.
     * @param trackId the id of the song to fetch
     * @return the song fetched
     */
    suspend fun fetchSong(
        trackId: String,
        onSuccess: (Song) -> Unit,
    ) {
        Log.d(TAG, "fetchSong trackId: $trackId")
        if(trackId.isEmpty()) return
        val song = songsDao.getSongById(trackId).value
        Log.d(TAG, "fetchSong song: $song")
        if (song != null) {
            onSuccess(song)
            return
        }
        Log.d(TAG, "fetchSong song from network")
        val track = networkService.getTrack(getAuthToken(), trackId)
        Log.d(SongDetailsFragmentViewModel.TAG, "fetchSong track: $track")
        val songFromTrack = track.toSong()
        Log.d(SongDetailsFragmentViewModel.TAG, "fetchSong songFromTrack: $songFromTrack")
        songsDao.insert(songFromTrack)
        onSuccess(songFromTrack)
    }

    /**
     * Fetch a search
     */
    suspend fun fetchSearch(
        query: String,
        onSuccess: (List<Song>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        try {
            Log.d(TAG, "fetchSearch query: $query")
            if(query.isEmpty()) return
            val networkSongs = networkService.search(getAuthToken(), query)
            val items = networkSongs.tracks?.items
            val fetchedSongs = items?.map(Items::toSong) ?: throw Exception("Unable to fetch data from API", null)
            val songs = fetchedSongs.filter { song -> song.previewUrl != null }
            songsDao.insertAll(songs)
            onSuccess(songs)
        } catch (e: Exception) {
            Log.e(TAG, "fetchSearch: ${e.message}")
            onError(e.message ?: "Unable to fetch data from API")
        }
    }

    /**
     * Fetch the recommendations
     */
    suspend fun fetchRecommendations(
        artistSeed: String? = null,
        genreSeed: String? = null,
        onSuccess: (List<Song>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        try {
            if (artistSeed.isNullOrEmpty() && genreSeed.isNullOrEmpty()) {
                val recommendations = networkService.getRecommendations(getAuthToken(), limit = 50)
                val tracks = recommendations.tracks
                val listOfSongs = tracks.map(Items::toSong)
                val songs = listOfSongs.filter { song -> song.previewUrl != null }
                onSuccess(songs)
            } else {
                val recommendations = networkService.getRecommendations(
                    getAuthToken(),
                    limit = 50,
                    seedTracks = "",
                    seedArtists = artistSeed ?: "",
                    seedGenres = genreSeed ?: ""
                )
                val tracks = recommendations.tracks
                val listOfSongs = tracks.map(Items::toSong)
                val songs = listOfSongs.filter { song -> song.previewUrl != null }
                onSuccess(songs)
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchRecommendations: ${e.message}")
            onError(e.message ?: "Unable to fetch data from API")
        }
    }

}

