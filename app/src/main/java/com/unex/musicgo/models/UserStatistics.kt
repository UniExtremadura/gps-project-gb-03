package com.unex.musicgo.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * This class represents a UserStatistics.
 *
 * @property songId the song id
 * @property title the song title
 * @property artist the song artist
 * @property plays the number of plays
 * @property timePlayed the time played in milliseconds
 */
@Entity(
    tableName = "statistics_song",
    indices = [Index(value = ["songId"], unique = true)]
)
data class UserStatistics(
    @PrimaryKey
    val songId: String,
    val title: String,
    val artist: String,
    val plays: Int = 0,
    val timePlayed: Long = 0
) : Serializable