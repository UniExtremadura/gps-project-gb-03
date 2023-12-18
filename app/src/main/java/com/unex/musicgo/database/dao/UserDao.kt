package com.unex.musicgo.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unex.musicgo.models.User

@Dao
interface UserDao {

    /**
     * Insert a new user into the database.
     * @param user the user to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    /**
     * Get a user by its email.
     * @param email the email of the user to be retrieved.
     * @return the user with the given username.
     */
    @Query("SELECT * FROM user WHERE email = :email")
    fun getUserByEmail(email: String): LiveData<User>

    /**
     * Delete all the users from the database.
     */
    @Query("DELETE FROM user")
    suspend fun deleteAll()

}