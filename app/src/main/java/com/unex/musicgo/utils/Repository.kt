package com.unex.musicgo.utils

import com.google.firebase.auth.FirebaseAuth
import com.unex.musicgo.api.AuthAPI
import com.unex.musicgo.api.MusicGoAPI
import com.unex.musicgo.database.MusicGoDatabase

class Repository(
    private val authService: AuthAPI,
    private val networkService: MusicGoAPI,
    private val db: MusicGoDatabase
) {
    val database = db
    val auth = FirebaseAuth.getInstance()
}