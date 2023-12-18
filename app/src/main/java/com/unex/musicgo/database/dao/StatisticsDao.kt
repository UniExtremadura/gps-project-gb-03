package com.unex.musicgo.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.unex.musicgo.models.UserStatistics

@Dao
interface StatisticsDao {

    /**
     * Register a new play of a song. This is used to update the statistics of a song.
     * @param songId the song id.
     * @param title the song title.
     * @param artist the song artist.
     * @param timePlayed the time played in milliseconds.
     */
    @Query("INSERT OR REPLACE INTO statistics_song (songId, title, artist, plays, timePlayed) VALUES (:songId, :title, :artist, IFNULL((SELECT plays FROM statistics_song WHERE songId = :songId), 0) + 1, IFNULL((SELECT timePlayed FROM statistics_song WHERE songId = :songId), 0) + :timePlayed)")
    suspend fun registerPlay(songId: String, title: String, artist: String, timePlayed: Long)

    /**
     * Get the biggest statistic of plays. This is used to get the most played song.
     * @return the songId of the most played song.
     */
    @Query("SELECT * FROM statistics_song ORDER BY plays DESC LIMIT 1")
    fun getMostPlayedSong(): LiveData<UserStatistics>

    /**
     * Get the biggest statistic of plays. This is used to get the most played song.
     * @param limit the number of songs to get.
     * @return statistics of the most played song.
     */
    @Query("SELECT * FROM statistics_song ORDER BY plays DESC LIMIT :limit")
    fun getAllMostPlayedSong(limit: Int): LiveData<List<UserStatistics>>

    /**
     * Get the biggest artist of plays. This is used to get the most played artist.
     * @return the artistId of the most played artist.
     */
    @Query("SELECT * FROM statistics_song ORDER BY plays DESC LIMIT 1")
    fun getMostPlayedArtist(): LiveData<UserStatistics>

    /**
     * Get the total time played of the user. This is used to get the total time played.
     * @return the total time played.
     */
    @Query("SELECT SUM(timePlayed) FROM statistics_song")
    fun getTotalTimePlayed(): LiveData<Long>

}