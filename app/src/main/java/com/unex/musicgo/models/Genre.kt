package com.unex.musicgo.models

import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * This class represents a song.
 *
 * Each song has a title, an artist, an album, a duration and a path.
 */
data class Genre(
    @PrimaryKey val title: String
) : Serializable