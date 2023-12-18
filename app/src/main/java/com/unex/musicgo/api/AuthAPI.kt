package com.unex.musicgo.api

import android.os.Build
import com.unex.musicgo.data.api.auth.AuthResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.Base64

private const val clientId = "e9d9aff4973a43faa4514c21ec12d013"
private const val clientSecret = "6914b73871a04468be9af5f8c73331d7"

private val auth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    "Basic " + Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())
} else {
    "Basic " + android.util.Base64.encodeToString("$clientId:$clientSecret".toByteArray(), android.util.Base64.NO_WRAP)
}

private val service: AuthAPI by lazy {
    val baseURL = "https://accounts.spotify.com/api/"
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor())
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    retrofit.create(AuthAPI::class.java)
}

fun getAuthService() = service

suspend fun getAuthToken(): String {
    val response = service.getToken(auth, "client_credentials")
    return "Bearer ${response.accessToken}"
}

interface AuthAPI {
    @FormUrlEncoded
    @POST("token")
    suspend fun getToken(@Header("Authorization") auth: String, @Field("grant_type") grantType: String): AuthResponse
}

class APIAuthError(message: String, cause: Throwable?) : Throwable(message, cause)

interface APIAuthCallback {
    fun onCompleted(authToken: AuthResponse)
    fun onError(cause: Throwable)
}