package com.unex.musicgo.data.api.search

import com.google.gson.annotations.SerializedName
import com.unex.musicgo.data.api.common.Albums
import com.unex.musicgo.data.api.common.Artists
import com.unex.musicgo.data.api.common.Audiobooks
import com.unex.musicgo.data.api.common.Episodes
import com.unex.musicgo.data.api.common.Playlists
import com.unex.musicgo.data.api.common.Seeds
import com.unex.musicgo.data.api.common.Shows
import com.unex.musicgo.data.api.common.Tracks


data class SearchResponse (

    @SerializedName("tracks"     ) var tracks     : Tracks?     = Tracks(),
    @SerializedName("artists"    ) var artists    : Artists?    = Artists(),
    @SerializedName("albums"     ) var albums     : Albums?     = Albums(),
    @SerializedName("playlists"  ) var playlists  : Playlists?  = Playlists(),
    @SerializedName("shows"      ) var shows      : Shows?      = Shows(),
    @SerializedName("episodes"   ) var episodes   : Episodes?   = Episodes(),
    @SerializedName("audiobooks" ) var audiobooks : Audiobooks? = Audiobooks(),
    @SerializedName("seeds"      ) var seeds  : ArrayList<Seeds>  = arrayListOf()

)