package com.unex.musicgo.ui.interfaces

import android.view.View
import com.unex.musicgo.models.Song

interface OnSongClickListener {
    fun onSongClick(song: Song)
    fun onOptionsClick(song: Song, view: View)
}