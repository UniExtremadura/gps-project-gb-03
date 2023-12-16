package com.unex.musicgo.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    @DisplayName("Test User Creation")
    fun createUserObject() {
        val user = User(
            email = "test@example.com",
            username = "testUser",
            userSurname = "TestSurname"
        )

        assertNotNull(user)
        assertEquals("test@example.com", user.email)
        assertEquals("testUser", user.username)
        assertEquals("TestSurname", user.userSurname)
    }

    @Test
    @DisplayName("Test default values")
    fun defaultValuesAreSet() {
        val user = User(
            email = "default@example.com",
            username = "defaultUser",
            userSurname = "DefaultSurname"
        )

        assertNotNull(user)
        assertEquals("default@example.com", user.email)
        assertEquals("defaultUser", user.username)
        assertEquals("DefaultSurname", user.userSurname)
    }
}
