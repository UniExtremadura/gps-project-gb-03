package com.unex.musicgo.data.api.common

import com.google.gson.annotations.SerializedName


data class Artists (

  @SerializedName("external_urls" ) var externalUrls : ExternalUrls?     = ExternalUrls(),
  @SerializedName("href"          ) var href         : String?           = null,
  @SerializedName("id"            ) var id           : String?           = null,
  @SerializedName("name"          ) var name         : String?           = null,
  @SerializedName("type"          ) var type         : String?           = null,
  @SerializedName("uri"           ) var uri          : String?           = null,
  @SerializedName("genres"        ) var genres       : ArrayList<String> = arrayListOf(),
  @SerializedName("items"         ) var items        : ArrayList<Items>  = arrayListOf(),
  @SerializedName("followers"     ) var followers    : Followers?        = Followers(),
  @SerializedName("images"        ) var images       : ArrayList<Images> = arrayListOf(),
  @SerializedName("popularity"    ) var popularity   : Int?              = null,

  )