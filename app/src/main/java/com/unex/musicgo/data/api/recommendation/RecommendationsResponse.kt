package com.unex.musicgo.data.api.recommendation

import com.google.gson.annotations.SerializedName
import com.unex.musicgo.data.api.common.Items
import com.unex.musicgo.data.api.common.Seeds


data class RecommendationsResponse (

    @SerializedName("seeds"  ) var seeds  : ArrayList<Seeds>  = arrayListOf(),
    @SerializedName("tracks" ) var tracks : ArrayList<Items> = arrayListOf()

)