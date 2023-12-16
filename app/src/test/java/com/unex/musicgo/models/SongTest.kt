package com.unex.musicgo.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SongTest {

    @Test
    @DisplayName("Test Song Creation")
    fun testComment() {
        val song = Song("id1", "title1", "artist1", 123.456, "coverPath1", "previewUrl1", "genres1", 123456789L, true, 5)

        assertEquals("id1", song.id)
        assertEquals("title1", song.title)
        assertEquals("artist1", song.artist)
        assertEquals(123.456, song.duration)
        assertEquals("coverPath1", song.coverPath)
        assertEquals("previewUrl1", song.previewUrl)
        assertEquals("genres1", song.genres)
        assertEquals(123456789L, song.cacheTimestamp)
        assertTrue(song.isRated)
        assertEquals(5, song.rating)
    }

    @Test
    @DisplayName("Test Song Copy")
    fun testCopy() {
        val song1 = Song("id1", "title1", "artist1", 123.456, "coverPath1", "previewUrl1", "genres1", 123456789L, true, 5)
        val song2 = song1.copy()

        assertEquals(song1, song2)
    }

    @Test
    @DisplayName("Test Song Equality")
    fun testEquals() {
        val song1 = Song("id1", "title1", "artist1", 123.456, "coverPath1", "previewUrl1", "genres1", 123456789L, true, 5)
        val song2 = Song("id1", "title1", "artist1", 123.456, "coverPath1", "previewUrl1", "genres1", 123456789L, true, 5)
        val song3 = Song("id2", "title2", "artist2", 654.321, "coverPath2", "previewUrl2", "genres2", 987654321L, false, 0)

        assertTrue(song1 == song2)
        assertFalse(song1 == song3)
    }

    @Test
    @DisplayName("Test Song Hash Code")
    fun testHashCode() {
        val song1 = Song("id1", "title1", "artist1", 123.456, "coverPath1", "previewUrl1", "genres1", 123456789L, true, 5)
        val song2 = Song("id1", "title1", "artist1", 123.456, "coverPath1", "previewUrl1", "genres1", 123456789L, true, 5)
        val song3 = Song("id2", "title2", "artist2", 654.321, "coverPath2", "previewUrl2", "genres2", 987654321L, false, 0)

        assertEquals(song1.hashCode(), song2.hashCode())
        assertNotEquals(song1.hashCode(), song3.hashCode())
    }

    @Test
    @DisplayName("Test Song Rating")
    fun testRating() {
        val song = Song("id1", "title1", "artist1", 123.456, "coverPath1", "previewUrl1", "genres1", 123456789L, true, 5)

        assertEquals(5, song.rating)
        song.rating = 3
        assertEquals(3, song.rating)
    }
}