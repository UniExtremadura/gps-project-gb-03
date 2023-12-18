package com.unex.musicgo.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.unex.musicgo.models.PlayList
import com.unex.musicgo.models.PlayListWithSongs

@Dao
interface PlayListDao {

    /**
     * Insert a PlayList in the database. The PlayList must not already exist.
     * @param playlist the PlayList to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(playlist: PlayList): Long

    /**
     * Insert a list of PlayLists in the database. The PlayLists must not already exist.
     * @param playlists the PlayLists to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(playlists: List<PlayList>)

    /**
     * Update a PlayList information.
     * @param playlist the PlayList to be updated
     */
    @Update
    suspend fun update(playlist: PlayList)

    /**
     * Delete all PlayLists from the database.
     */
    @Query("DELETE FROM playlists")
    suspend fun deleteAll()

    /**
     * Delete all PlayLists from the database created by the user.
     */
    @Query("DELETE FROM playlists WHERE createdByUser = 1")
    suspend fun deleteAllCreatedByUser()

    /**
     * Delete a PlayList from the database.
     * @param playListId to be deleted
     */
    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun delete(playlistId: Int)

    /**
     * Get all PlayLists created by the user without the songs from the database.
     */
    @Query("SELECT * FROM playlists WHERE createdByUser = 1")
    fun getPlayListsCreatedByUserWithoutSongs(): LiveData<List<PlayList>>

    /**
     * Get the Recent PlayList from the database.
     * @return the PlayList with the title Recent and createdByUser to false.
     */
    @Transaction
    @Query("SELECT * FROM playlists WHERE title = 'Recent' AND createdByUser = 0")
    fun getRecentPlayList(): LiveData<PlayListWithSongs>

    /**
     * Get the Favorites PlayList from the database.
     * @return the PlayList with the title Favorites and createdByUser to false.
     */
    @Transaction
    @Query("SELECT * FROM playlists WHERE title = 'Favorites' AND createdByUser = 0")
    fun getFavoritesPlayList(): LiveData<PlayListWithSongs>

    /**
     * Get the Recommendations PlayList from the database.
     * @return the PlayList with the title Recommendations and createdByUser to false.
     */
    @Transaction
    @Query("SELECT * FROM playlists WHERE title = 'Recommendations' AND createdByUser = 0")
    fun getRecommendationsPlayList(): LiveData<PlayListWithSongs>

    /**
     * Get a PlayList from the database.
     * @param playlistTitle the title of the PlayList to be retrieved.
     * @param playlistDescription the description of the PlayList to be retrieved.
     */
    @Transaction
    @Query("SELECT * FROM playlists WHERE title = :title AND description = :description")
    fun getPlayList(title: String, description: String): LiveData<PlayListWithSongs>

    /**
     * Get a PlayList from the database.
     * @param playlistId the id of the PlayList to be retrieved.
     */
    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlayList(playlistId: Int): LiveData<PlayListWithSongs>

    /**
     * Get the number of playlists in the database.
     * @return the number of playlists.
     */
    @Query("SELECT COUNT(*) FROM playlists")
    suspend fun getPlayListsCount(): Int

}