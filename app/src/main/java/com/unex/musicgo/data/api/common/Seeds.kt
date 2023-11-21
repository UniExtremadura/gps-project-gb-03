package com.unex.musicgo.data.api.common

import com.google.gson.annotations.SerializedName


data class Seeds (

  @SerializedName("afterFilteringSize" ) var afterFilteringSize : Int?    = null,
  @SerializedName("afterRelinkingSize" ) var afterRelinkingSize : Int?    = null,
  @SerializedName("href"               ) var href               : String? = null,
  @SerializedName("id"                 ) var id                 : String? = null,
  @SerializedName("initialPoolSize"    ) var initialPoolSize    : Int?    = null,
  @SerializedName("type"               ) var type               : String? = null

)