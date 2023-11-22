package com.unex.musicgo.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * This class represents a PlayList.
 *
 * Each PlayList has a title.
 */
@Entity(tableName = "playlists")
data class PlayList (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var description: String,
    val createdByUser: Boolean = true
): Serializable