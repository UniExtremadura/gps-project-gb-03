package com.unex.musicgo.data.api.search

import com.google.gson.annotations.SerializedName

data class AvailableGenresResponse(
    @SerializedName("genres") var genres: ArrayList<String> = arrayListOf()
)