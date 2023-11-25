package com.unex.musicgo.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unex.musicgo.database.dao.PlayListDao
import com.unex.musicgo.database.dao.PlayListSongCrossRefDao
import com.unex.musicgo.database.dao.SongsDao
import com.unex.musicgo.database.dao.StatisticsDao
import com.unex.musicgo.database.dao.UserDao
import com.unex.musicgo.models.PlayList
import com.unex.musicgo.models.PlayListSongCrossRef
import com.unex.musicgo.models.Song
import com.unex.musicgo.models.User
import com.unex.musicgo.models.UserStatistics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Song::class,
        PlayList::class,
        PlayListSongCrossRef::class,
        UserStatistics::class,
        User::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MusicGoDatabase : RoomDatabase() {

    abstract fun songsDao(): SongsDao
    abstract fun playListDao(): PlayListDao
    abstract fun playListSongCrossRefDao(): PlayListSongCrossRefDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun userDao(): UserDao

    companion object {
        const val TAG = "MusicGoDatabase"
        private var INSTANCE: MusicGoDatabase? = null

        fun getInstance(context: Context): MusicGoDatabase? {
            if (INSTANCE == null) {
                Log.d(TAG, "Creating database instance")
                synchronized(MusicGoDatabase::class) {
                    Log.d(TAG, "Building database...")
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MusicGoDatabase::class.java,
                        "musicgo.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    Log.d(TAG, "Database built")
                    if (INSTANCE != null) {
                        Log.d(TAG, "Database instance created")
                        CoroutineScope(Dispatchers.IO).launch {
                            val numberOfPlaylists = INSTANCE!!.playListDao().getPlayListsCount()
                            if (numberOfPlaylists == 0) {
                                Log.d(TAG, "Inserting default playlists...")
                                INSTANCE!!.playListDao().insertAll(DEFAULT_PLAYLISTS)
                                Log.d(TAG, "Default playlists inserted")
                            }
                        }
                    }
                }
            }
            return INSTANCE
        }

        private val DEFAULT_PLAYLISTS = listOf(
            PlayList(title = "Recent", description = "", createdByUser = false),
            PlayList(title = "Favorites", description = "", createdByUser = false)
        )
    }

}