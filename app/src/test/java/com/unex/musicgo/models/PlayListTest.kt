package com.unex.musicgo.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PlayListTest {

    @Test
    @DisplayName("Test Playlist Creation")
    fun createPlaylistObject() {
        val playlist = PlayList(
            title = "My Playlist",
            description = "This is a test playlist",
            createdByUser = true
        )

        assertNotNull(playlist)
        assertEquals("My Playlist", playlist.title)
        assertEquals("This is a test playlist", playlist.description)
        assertTrue(playlist.createdByUser)
    }

    @Test
    @DisplayName("Test Default Values")
    fun defaultValuesAreSet() {
        val playlist = PlayList(
            title = "Default Playlist",
            description = "Default description"
        )

        assertNotNull(playlist)
        assertEquals("Default Playlist", playlist.title)
        assertEquals("Default description", playlist.description)
        assertTrue(playlist.createdByUser) // Since it's true by default
    }
}
