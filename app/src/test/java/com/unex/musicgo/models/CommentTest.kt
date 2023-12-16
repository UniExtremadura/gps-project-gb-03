package com.unex.musicgo.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CommentTest {

    @Test
    @DisplayName("Test Comment Creation")
    fun testComment() {
        val comment = Comment("songId1", "authorEmail1", "username1", "description1", 123456789L)

        assertEquals("songId1", comment.songId)
        assertEquals("authorEmail1", comment.authorEmail)
        assertEquals("username1", comment.username)
        assertEquals("description1", comment.description)
        assertEquals(123456789L, comment.timestamp)
    }

    @Test
    @DisplayName("Test Comment Copy")
    fun testCopy() {
        val comment1 = Comment("songId1", "authorEmail1", "username1", "description1", 123456789L)
        val comment2 = comment1.copy()

        assertEquals(comment1, comment2)
    }

    @Test
    @DisplayName("Test Comment Equality")
    fun testEquals() {
        val comment1 = Comment("songId1", "authorEmail1", "username1", "description1", 123456789L)
        val comment2 = Comment("songId1", "authorEmail1", "username1", "description1", 123456789L)
        val comment3 = Comment("songId2", "authorEmail2", "username2", "description2", 987654321L)

        assertTrue(comment1 == comment2)
        assertFalse(comment1 == comment3)
    }

    @Test
    @DisplayName("Test Comment Hash Code")
    fun testHashCode() {
        val comment1 = Comment("songId1", "authorEmail1", "username1", "description1", 123456789L)
        val comment2 = Comment("songId1", "authorEmail1", "username1", "description1", 123456789L)
        val comment3 = Comment("songId2", "authorEmail2", "username2", "description2", 987654321L)

        assertEquals(comment1.hashCode(), comment2.hashCode())
        assertNotEquals(comment1.hashCode(), comment3.hashCode())
    }
}