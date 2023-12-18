package com.unex.musicgo.utils

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unex.musicgo.api.getAuthService
import com.unex.musicgo.api.getNetworkService
import com.unex.musicgo.database.MusicGoDatabase

class AppContainer(context: Context?) {
    private val authService = getAuthService()
    private val networkService = getNetworkService()
    private val db = MusicGoDatabase.getInstance(context!!)!!
    private val userDao = db.userDao()
    private val songsDao = db.songsDao()
    private val playListDao = db.playListDao()
    private val statisticsDao = db.statisticsDao()
    private val playListSongCrossRefDao = db.playListSongCrossRefDao()

    val repository = Repository(
        authService,
        networkService,
        db
    )

    val userRepository = UserRepository(
        Firebase.auth,
        userDao
    )

    val songRepository = SongRepository(
        networkService,
        songsDao
    )

    val statisticsRepository = StatisticsRepository(
        statisticsDao
    )

    val playListRepository = PlayListRepository(
        songsDao,
        playListDao,
        playListSongCrossRefDao
    )
}