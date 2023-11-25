package com.unex.musicgo.data.api.common

import com.google.gson.annotations.SerializedName


data class ExternalIds (

  @SerializedName("isrc" ) var isrc : String? = null,
  @SerializedName("ean"  ) var ean  : String? = null,
  @SerializedName("upc"  ) var upc  : String? = null

)