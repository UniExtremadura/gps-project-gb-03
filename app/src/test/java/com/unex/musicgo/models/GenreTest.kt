package com.unex.musicgo.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GenreTest {

    @Test
    @DisplayName("Test Genre Creation")
    fun createGenreObject() {
        val genre = Genre(title = "Rock")

        assertNotNull(genre)
        assertEquals("Rock", genre.title)
    }

    @Test
    @DisplayName("Test Default Values Set")
    fun defaultValuesAreSet() {
        val genre = Genre(title = "Default Genre")

        assertNotNull(genre)
        assertEquals("Default Genre", genre.title)
    }


    @Test
    @DisplayName("Test Equality Check")
    fun equalityCheck() {
        val genre1 = Genre(title = "Rock")
        val genre2 = Genre(title = "Rock")
        val genre3 = Genre(title = "Pop")

        assertEquals(genre1, genre2) // Same titles, should be equal
        assertNotEquals(genre1, genre3) // Different titles, should not be equal
    }
}
