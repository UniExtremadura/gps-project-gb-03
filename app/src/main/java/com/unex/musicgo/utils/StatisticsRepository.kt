package com.unex.musicgo.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.unex.musicgo.database.dao.StatisticsDao
import com.unex.musicgo.models.UserStatistics

class StatisticsRepository(
    private val statisticsDao: StatisticsDao
) {
    private val TAG = "StatisticsRepository"

    val songStatistics : LiveData<String> = statisticsDao.getMostPlayedSong().switchMap {
        MutableLiveData(it.title)
    }
    val artistStatistics : LiveData<String> = statisticsDao.getMostPlayedArtist().switchMap {
        MutableLiveData(it.artist)
    }
    val timeStatistics : LiveData<String> = statisticsDao.getTotalTimePlayed().switchMap {
        MutableLiveData(transformTime(it))
    }
    val songArtistStatistics : LiveData<String> = statisticsDao.getMostPlayedSong().switchMap {
        MutableLiveData(it.artist)
    }

    private val _limit = MutableLiveData<Int>()
    val mostPlayedSongs : LiveData<List<UserStatistics>> = _limit.switchMap {
        statisticsDao.getAllMostPlayedSong(it)
    }

    fun fetchMostPlayedSong(limit: Int? = null) {
        limit?.let {
            _limit.postValue(it)
        }
    }

    private fun transformTime(time: Long): String {
        val days = time / 86400000
        val hours = (time % 86400000) / 3600000
        val minutes = (time % 3600000) / 60000
        val seconds = (time % 60000) / 1000
        return if (days > 0) {
            "$days days, $hours hours, $minutes minutes and $seconds seconds"
        } else if (hours > 0) {
            "$hours hours, $minutes minutes and $seconds seconds"
        } else if (minutes > 0) {
            "$minutes minutes and $seconds seconds"
        } else {
            "$seconds seconds"
        }
    }
}