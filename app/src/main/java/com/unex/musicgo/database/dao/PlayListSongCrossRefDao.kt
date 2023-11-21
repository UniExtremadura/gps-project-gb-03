package com.unex.musicgo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unex.musicgo.models.PlayListSongCrossRef

@Dao
interface PlayListSongCrossRefDao {

    /**
     * Insert a relation between a playlist and a song.
     *
     * @param playlistId the playlist id
     * @param songId the song id
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playListSongCrossRef: PlayListSongCrossRef)

    /**
     * Insert a list of relations between a playlist and its songs.
     * @param playListSongCrossRefList the list of relations
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playListSongCrossRefList: List<PlayListSongCrossRef>)

    /**
     * Delete a relation between a playlist and a song.
     *
     * @param playlistId the playlist id
     * @param songId the song id
     */
    @Query("DELETE FROM PlayListSongCrossRef WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun delete(playlistId: Int, songId: String)

    /**
     * Delete a list of relations between a playlist and its songs.
     *
     * @param playListSongCrossRefList the list with the relations.
     */
    @Query("DELETE FROM PlayListSongCrossRef WHERE playlistId = :playlistId AND songId IN (:songIds)")
    suspend fun deleteAll(playlistId: Int, songIds: List<String>)

    /**
     * Delete all relations between a playlist and its songs.
     *
     * @param playlistId the playlist id
     */
    @Query("DELETE FROM PlayListSongCrossRef WHERE playlistId = :playlistId")
    suspend fun deleteAllByPlayList(playlistId: Int)

    /**
     * Delete all relations between a song and its playlists.
     *
     * @param songId the song id
     */
    @Query("DELETE FROM PlayListSongCrossRef WHERE songId = :songId")
    suspend fun deleteAllBySong(songId: String)

}