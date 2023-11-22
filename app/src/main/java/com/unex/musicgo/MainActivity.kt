package com.unex.musicgo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    companion object {
        const val TAG = "MainActivity"

        fun getIntent(context: AppCompatActivity) =
            android.content.Intent(context, MainActivity::class.java)
    }
}