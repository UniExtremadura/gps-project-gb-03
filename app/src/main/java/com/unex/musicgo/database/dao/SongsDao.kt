package com.unex.musicgo.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unex.musicgo.models.Song

@Dao
interface SongsDao {

    /**
     * Insert a song in the database. The song must not already exist.
     * @param song the song to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: Song)

    /**
     * Insert a list of songs in the database. The songs must not already exist.
     * @param songs the songs to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(songs: List<Song>)

    /**
     * Delete a song from the database.
     * @param songId of the song to delete
     */
    @Query("DELETE FROM songs WHERE id = :songId")
    suspend fun delete(songId: String)

    /**
     * Delete a list of songs from the database.
     * @param songsIds of the songs to delete
     */
    @Query("DELETE FROM songs WHERE id IN (:songsIds)")
    suspend fun deleteAll(songsIds: List<String>)

    /**
     * Get a song by its id.
     */
    @Query("SELECT * FROM songs WHERE id = :songId")
    fun getSongById(songId: String): LiveData<Song?>

    /**
     * Get a list of songs by the query.
     * @param query the query to search
     */
    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    fun searchByQuery(query: String): LiveData<List<Song>>

    /**
     * Clear the cache of the songs.
     *
     * Delete all songs that are older than the given timestamp. Keep all of them that are in a playlist.
     *
     * The default timestamp is 7 days ago. (604800000 milliseconds)
     */
    @Query("DELETE FROM songs WHERE cacheTimestamp < :timestamp AND id NOT IN (SELECT songId FROM PlayListSongCrossRef)")
    suspend fun clearCache(timestamp: Long = System.currentTimeMillis() - 604800000)

}