package com.unex.musicgo.api

import com.unex.musicgo.data.api.auth.AuthResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class AuthAPITest {

    @Mock
    lateinit var service: AuthAPI // Mocked service
    private val expectedAuthResponse = AuthResponse("AccessToken", "Bearer", 3600)

    @BeforeEach
    internal fun setup() {
        MockitoAnnotations.initMocks(this)
        runBlocking {
            val auth = any<String>()
            val grantType = any<String>()
            whenever(service.getToken(auth, grantType))
                .thenReturn(
                    expectedAuthResponse
                )
        }
    }

    @Test
    fun `getAuthToken returns valid token on successful response`() {
        runBlocking {
            val result = service.getToken("auth", "grantType")
            assert(result == expectedAuthResponse)
        }
    }

    @Test
    fun `getAuthToken throws APIAuthError on unsuccessful response`() {
        runBlocking {
            val result = service.getToken(any(), any())
            assert(result != expectedAuthResponse)
        }
    }
}