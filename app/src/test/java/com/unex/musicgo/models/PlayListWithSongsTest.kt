package com.unex.musicgo.models

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class PlayListWithSongsTest {
    @Test
    fun `getPlaylist should return the correct playlist`() {
        val playList = PlayList(id = 1, title = "My Playlist", description = "Description")
        val songs = listOf(
            Song(id = "101", title = "Song 1", artist = "Artist 1", duration = 180.0),
            Song(id = "102", title = "Song 2", artist = "Artist 2", duration = 240.0)
        )
        val playListWithSongs = PlayListWithSongs(playList, songs)

        assertEquals(playList, playListWithSongs.playlist)
    }

    @Test
    fun `getSongs should return the correct list of songs`() {
        val playList = PlayList(id = 1, title = "My Playlist", description = "Description")
        val songs = listOf(
            Song(id = "101", title = "Song 1", artist = "Artist 1", duration = 180.0),
            Song(id = "102", title = "Song 2", artist = "Artist 2", duration = 240.0)
        )
        val playListWithSongs = PlayListWithSongs(playList, songs)

        assertEquals(songs, playListWithSongs.songs)
    }

    @Test
    fun `setSongs should update the list of songs`() {
        val playList = PlayList(id = 1, title = "My Playlist", description = "Description")
        val songs = listOf(
            Song(id = "101", title = "Song 1", artist = "Artist 1", duration = 180.0),
            Song(id = "102", title = "Song 2", artist = "Artist 2", duration = 240.0)
        )
        val playListWithSongs = PlayListWithSongs(playList, songs)

        val newSongs = listOf(
            Song(id = "103", title = "Song 3", artist = "Artist 3", duration = 200.0),
            Song(id = "104", title = "Song 4", artist = "Artist 4", duration = 220.0)
        )

        playListWithSongs.songs = newSongs

        assertEquals(newSongs, playListWithSongs.songs)
    }

    @Test
    fun `testEquals should correctly compare two PlayListWithSongs objects`() {
        val playList1 = PlayList(id = 1, title = "My Playlist", description = "Description")
        val songs1 = listOf(
            Song(id = "101", title = "Song 1", artist = "Artist 1", duration = 180.0),
            Song(id = "102", title = "Song 2", artist = "Artist 2", duration = 240.0)
        )
        val playListWithSongs1 = PlayListWithSongs(playList1, songs1)

        val playList2 = PlayList(id = 2, title = "Another Playlist", description = "Description")
        val songs2 = listOf(
            Song(id = "103", title = "Song 3", artist = "Artist 3", duration = 200.0),
            Song(id = "104", title = "Song 4", artist = "Artist 4", duration = 220.0)
        )
        val playListWithSongs2 = PlayListWithSongs(playList2, songs2)

        assertNotEquals(playListWithSongs1, playListWithSongs2)
    }


    @Test
    fun `component1 should return the correct value`() {
        val playList = PlayList(id = 1, title = "My Playlist", description = "Description")
        val songs = listOf(
            Song(id = "101", title = "Song 1", artist = "Artist 1", duration = 180.0),
            Song(id = "102", title = "Song 2", artist = "Artist 2", duration = 240.0)
        )
        val playListWithSongs = PlayListWithSongs(playList, songs)

        assertEquals(playList, playListWithSongs.component1())
    }

    @Test
    fun `component2 should return the correct value`() {
        val playList = PlayList(id = 1, title = "My Playlist", description = "Description")
        val songs = listOf(
            Song(id = "101", title = "Song 1", artist = "Artist 1", duration = 180.0),
            Song(id = "102", title = "Song 2", artist = "Artist 2", duration = 240.0)
        )
        val playListWithSongs = PlayListWithSongs(playList, songs)

        assertEquals(songs, playListWithSongs.component2())
    }

    @Test
    fun `copy should create an equal copy`() {
        val playList = PlayList(id = 1, title = "My Playlist", description = "Description")
        val songs = listOf(
            Song(id = "101", title = "Song 1", artist = "Artist 1", duration = 180.0),
            Song(id = "102", title = "Song 2", artist = "Artist 2", duration = 240.0)
        )
        val playListWithSongs = PlayListWithSongs(playList, songs)

        val copiedPlayListWithSongs = playListWithSongs.copy()

        assertEquals(playListWithSongs, copiedPlayListWithSongs)
    }

    @Test
    fun `testToString should return a non-empty string`() {
        val playList = PlayList(id = 1, title = "My Playlist", description = "Description")
        val songs = listOf(
            Song(id = "101", title = "Song 1", artist = "Artist 1", duration = 180.0),
            Song(id = "102", title = "Song 2", artist = "Artist 2", duration = 240.0)
        )
        val playListWithSongs = PlayListWithSongs(playList, songs)

        assertTrue(playListWithSongs.toString().isNotEmpty())
    }

    @Test
    fun `testHashCode should return a non-zero hash code`() {
        val playList = PlayList(id = 1, title = "My Playlist", description = "Description")
        val songs = listOf(
            Song(id = "101", title = "Song 1", artist = "Artist 1", duration = 180.0),
            Song(id = "102", title = "Song 2", artist = "Artist 2", duration = 240.0)
        )
        val playListWithSongs = PlayListWithSongs(playList, songs)

        assertNotEquals(0, playListWithSongs.hashCode())
    }
}