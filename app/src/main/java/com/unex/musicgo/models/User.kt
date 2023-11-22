package com.unex.musicgo.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * This class represents a user.
 *
 * @property email the email of the user.
 * @property username the username of the user.
 * @property userSurname the surname of the user.
 */
@Entity(tableName = "user")
data class User(
    @PrimaryKey val email: String,
    val username: String,
    val userSurname: String
) : Serializable