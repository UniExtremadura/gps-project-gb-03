package com.unex.musicgo.data.api.common

import com.google.gson.annotations.SerializedName


data class Tracks (

  @SerializedName("href"  ) var href  : String? = null,
  @SerializedName("total" ) var total : Int?    = null,
  @SerializedName("items" ) var items : ArrayList<Items>? = null

)