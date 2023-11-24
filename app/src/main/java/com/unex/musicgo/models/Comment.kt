package com.unex.musicgo.models

import androidx.room.Entity
import java.io.Serializable

/**
 * This class represents a song comment.
 *
 * @property songId the song id
 * @property authorEmail the comment author email
 * @property username the comment author username
 * @property description the comment description
 * @property timestamp the comment timestamp
 */
@Entity(
    tableName = "comments",
    primaryKeys = ["songId", "authorEmail", "timestamp"]
)
data class Comment(
    val songId: String,
    val authorEmail: String,
    val username: String,
    val description: String,
    val timestamp: Long
) : Serializable