package com.unex.musicgo.api

import com.unex.musicgo.data.api.common.Items
import com.unex.musicgo.data.api.recommendation.RecommendationsResponse
import com.unex.musicgo.data.api.search.AvailableGenresResponse
import com.unex.musicgo.data.api.search.SearchResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MusicGoAPITest {

    private val TAG = "MusicGoAPITest"

    @Mock
    lateinit var service: MusicGoAPI

    private val recommendationsExpected = RecommendationsResponse(
        seeds = arrayListOf(),
        tracks = arrayListOf(
            Items(
                id = "id",
                name = "name",
                artists = arrayListOf(),
                album = mock(),
                previewUrl = "previewUrl",
                durationMs = 1000,
                popularity = 100,
                isLocal = false
            )
        )
    )
    private val trackExpected = Items(
        id = "id",
        name = "name",
        artists = arrayListOf(),
        album = mock(),
        previewUrl = "previewUrl",
        durationMs = 1000,
        popularity = 100,
        isLocal = false
    )
    private val searchExpected = SearchResponse(
        seeds = arrayListOf()
    )
    private val availableGenresExpected = AvailableGenresResponse(
        genres = arrayListOf(
            "genre1",
            "genre2"
        )
    )

    @BeforeEach
    internal fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `getTrack returns valid track on successful response`() {
        runBlocking {
            val auth = "auth"
            val id = "id"
            whenever(service.getTrack(auth = auth, id = id))
                .thenReturn(trackExpected)
            val result = service.getTrack(
                auth = auth,
                id = id
            )
            assert(result.id == trackExpected.id)
        }
    }

    @Test
    fun `search returns valid response on successful search`() {
        runBlocking {
            val auth = "auth"
            val query = "query"
            val type = "type"
            val limit = 20
            whenever(
                service.search(
                    auth = auth,
                    query = query,
                    type = type,
                    limit = 20
                )
            )
                .thenReturn(searchExpected)
            val result = service.search(
                auth = auth,
                query = query,
                type = type,
                limit = limit
            )
            assert(result == searchExpected)
        }
    }

    @Test
    fun `getRecommendations returns valid recommendations on successful request`() {
        runBlocking {
            val auth = "auth"
            val limit = 20
            val market = "market"
            val seedTracks = "seedTracks"
            val seedArtists = "seedArtists"
            val seedGenres = "seedGenres"
            whenever(
                service.getRecommendations(
                    auth = auth,
                    limit = limit,
                    market = market,
                    seedTracks = seedTracks,
                    seedArtists = seedArtists,
                    seedGenres = seedGenres
                )
            )
                .thenReturn(recommendationsExpected)
            val result = service.getRecommendations(
                auth = auth,
                limit = limit,
                market = market,
                seedTracks = seedTracks,
                seedArtists = seedArtists,
                seedGenres = seedGenres
            )
            assert(result == recommendationsExpected)
        }
    }

    @Test
    fun `getAvailableGenres returns valid genres on successful request`() {
        runBlocking {
            val auth = "auth"
            whenever(
                service.getAvailableGenres(
                    auth = auth
                )
            )
                .thenReturn(availableGenresExpected)
            val result = service.getAvailableGenres(
                auth = auth
            )
            assert(result == availableGenresExpected)
        }
    }
}