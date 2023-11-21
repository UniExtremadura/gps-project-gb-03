package com.unex.musicgo.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import java.io.Serializable

/**
 * This class represents a PlayList with songs.
 *
 * Each PlayList has a title and a list of songs.
 */
data class PlayListWithSongs(
    @Embedded val playlist: PlayList,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PlayListSongCrossRef::class,
            parentColumn = "playListId",
            entityColumn = "songId"    
        )
    )
    var songs: List<Song>
): Serializable