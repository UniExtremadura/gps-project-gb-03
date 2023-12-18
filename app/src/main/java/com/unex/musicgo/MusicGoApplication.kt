package com.unex.musicgo

import android.app.Application
import com.unex.musicgo.utils.AppContainer

class MusicGoApplication: Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}