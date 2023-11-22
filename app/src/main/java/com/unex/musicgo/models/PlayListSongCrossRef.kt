package com.unex.musicgo.models

import androidx.room.Entity
import androidx.room.Index
import java.io.Serializable

/**
 * This class represents the relationship between a PlayList and a Song.
 *
 * Each PlayListSongCrossRef has a playlistId and a songId.
 */
@Entity(
    primaryKeys = ["playListId", "songId"],
    indices = [
        Index(value = ["playListId", "songId"]),
        Index(value = ["songId"])
    ]
)
data class PlayListSongCrossRef(
    val playListId: Int,
    val songId: String
) : Serializable