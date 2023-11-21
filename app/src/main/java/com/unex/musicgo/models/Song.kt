package com.unex.musicgo.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * This class represents a song.
 *
 * Each song has a title, an artist, an album, a duration and a path.
 */
@Entity(tableName = "songs")
data class Song (
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val duration: Double,
    val coverPath: String? = null,
    val previewUrl: String? = null,
    val genres: String? = null,
    val cacheTimestamp: Long = System.currentTimeMillis(),
    var isRated: Boolean = false,
    var rating: Int = 0
): Serializable