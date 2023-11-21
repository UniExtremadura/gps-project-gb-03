package com.unex.musicgo.data.api.common

import com.google.gson.annotations.SerializedName


data class Copyrights (

  @SerializedName("text" ) var text : String? = null,
  @SerializedName("type" ) var type : String? = null

)