package com.unex.musicgo.api

import com.unex.musicgo.data.api.common.Items
import com.unex.musicgo.data.api.search.SearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private val service: MusicGoAPI by lazy {

    val baseURL = "https://api.spotify.com/v1/"

    val httpInterceptor = HttpLoggingInterceptor()
    // .setLevel(HttpLoggingInterceptor.Level.BODY)

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpInterceptor)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    retrofit.create(MusicGoAPI::class.java)
}

fun getNetworkService() = service

interface MusicGoAPI {
    @GET("tracks/{id}")
    suspend fun getTrack(
        @Header("Authorization") auth: String,
        @Path("id") id: String
    ): Items

    @GET("search")
    suspend fun search(
        @Header("Authorization") auth: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 20
    ): SearchResponse
}

class APIError(message: String, cause: Throwable?) : Throwable(message, cause)

interface APICallback {
    fun onCompleted(tvShows: List<Items?>)
    fun onError(cause: Throwable)
}