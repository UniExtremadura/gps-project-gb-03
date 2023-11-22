package com.unex.musicgo.data

import android.util.Log
import com.unex.musicgo.data.api.common.Items
import com.unex.musicgo.models.Song
import java.util.UUID

fun Items.toSong() = Song(
    id = id ?: UUID.randomUUID().toString(),
    title = name ?: "Unknown",
    artist = getAuthor(this),
    duration = (durationMs ?: 0).toDouble(),
    coverPath = getCoverPath(this),
    previewUrl = previewUrl ?: "",
    genres = getGenres(item = this)
)

fun getGenres(item: Items? = null): String {
    Log.d("getGenres", "item: $item")
    val allArtists = mutableListOf<String>()
    item?.artists?.filter { it.genres.isNotEmpty() }?.let { allArtists.addAll(it.flatMap { it.genres }) }
    item?.album?.artists?.filter { it.genres.isNotEmpty() }?.let { allArtists.addAll(it.flatMap { it.genres }) }
    item?.genres?.filter { it.isNotEmpty() }?.let { allArtists.addAll(it) }

    return if (allArtists.isNotEmpty()) allArtists.joinToString(separator = ", ") else "Unknown"
}

fun getCoverPath(item: Items): String {
    val firstImageNotEmpty = item.images.firstOrNull()
    if (firstImageNotEmpty != null) {
        firstImageNotEmpty.url?.let { return it }
    }
    val firstAlbumImageNotEmpty = item.album?.images?.firstOrNull()
    if (firstAlbumImageNotEmpty != null) {
        firstAlbumImageNotEmpty.url?.let { return it }
    }
    return "https://www.lahiguera.net/musicalia/artistas/rosalia/disco/12797/rosalia_r8Я___con_rauw_alejandro-portada.jpg"
}

fun getAuthor(item: Items): String {
    val firstAuthorNotEmpty = item.authors.firstOrNull()
    if (firstAuthorNotEmpty != null) {
        firstAuthorNotEmpty.name?.let { return it }
    }
    val firstArtistNotEmpty = item.artists.firstOrNull()
    if (firstArtistNotEmpty != null) {
        firstArtistNotEmpty.name?.let { return it }
    }
    return "Unknown"
}