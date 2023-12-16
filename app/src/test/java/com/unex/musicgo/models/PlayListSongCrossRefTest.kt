package com.unex.musicgo.models

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class PlayListSongCrossRefTest {
    @Test
    fun `PlayListSongCrossRef creation with valid data`() {
        val playListSongCrossRef = PlayListSongCrossRef(1, "song1")
        assertEquals(1, playListSongCrossRef.playListId)
        assertEquals("song1", playListSongCrossRef.songId)
    }

    @Test
    fun `PlayListSongCrossRef equals with same data`() {
        val playListSongCrossRef1 = PlayListSongCrossRef(1, "song1")
        val playListSongCrossRef2 = PlayListSongCrossRef(1, "song1")
        assertEquals(playListSongCrossRef1, playListSongCrossRef2)
    }

    @Test
    fun `PlayListSongCrossRef equals with different data`() {
        val playListSongCrossRef1 = PlayListSongCrossRef(1, "song1")
        val playListSongCrossRef2 = PlayListSongCrossRef(2, "song2")
        assertNotEquals(playListSongCrossRef1, playListSongCrossRef2)
    }

    @Test
    fun `PlayListSongCrossRef hashCode with same data`() {
        val playListSongCrossRef1 = PlayListSongCrossRef(1, "song1")
        val playListSongCrossRef2 = PlayListSongCrossRef(1, "song1")
        assertEquals(playListSongCrossRef1.hashCode(), playListSongCrossRef2.hashCode())
    }

    @Test
    fun `PlayListSongCrossRef hashCode with different data`() {
        val playListSongCrossRef1 = PlayListSongCrossRef(1, "song1")
        val playListSongCrossRef2 = PlayListSongCrossRef(2, "song2")
        assertNotEquals(playListSongCrossRef1.hashCode(), playListSongCrossRef2.hashCode())
    }

    @Test
    fun getPlayListId() {
        val playListId = 1
        val songId = "song123"
        val playListSongCrossRef = PlayListSongCrossRef(playListId, songId)

        assertEquals(playListId, playListSongCrossRef.playListId)
    }

    @Test
    fun getSongId() {
        val playListId = 1
        val songId = "song123"
        val playListSongCrossRef = PlayListSongCrossRef(playListId, songId)

        assertEquals(songId, playListSongCrossRef.songId)
    }

    @Test
    fun component1() {
        val playListId = 1
        val songId = "song123"
        val playListSongCrossRef = PlayListSongCrossRef(playListId, songId)

        assertEquals(playListId, playListSongCrossRef.component1())
    }

    @Test
    fun component2() {
        val playListId = 1
        val songId = "song123"
        val playListSongCrossRef = PlayListSongCrossRef(playListId, songId)

        assertEquals(songId, playListSongCrossRef.component2())
    }

    @Test
    fun copy() {
        val playListId = 1
        val songId = "song123"
        val original = PlayListSongCrossRef(playListId, songId)

        val copied = original.copy(playListId = 2, songId = "song456")

        assertEquals(2, copied.playListId)
        assertEquals("song456", copied.songId)
    }

    @Test
    fun testToString() {
        val playListId = 1
        val songId = "song123"
        val playListSongCrossRef = PlayListSongCrossRef(playListId, songId)

        assertEquals(
            "PlayListSongCrossRef(playListId=$playListId, songId=$songId)",
            playListSongCrossRef.toString()
        )
    }
}