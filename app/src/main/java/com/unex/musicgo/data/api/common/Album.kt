package com.unex.musicgo.data.api.common

import com.google.gson.annotations.SerializedName


data class Album (

  @SerializedName("album_type"             ) var albumType            : String?            = null,
  @SerializedName("total_tracks"           ) var totalTracks          : Int?               = null,
  @SerializedName("available_markets"      ) var availableMarkets     : ArrayList<String>  = arrayListOf(),
  @SerializedName("external_urls"          ) var externalUrls         : ExternalUrls?      = ExternalUrls(),
  @SerializedName("href"                   ) var href                 : String?            = null,
  @SerializedName("id"                     ) var id                   : String?            = null,
  @SerializedName("images"                 ) var images               : ArrayList<Images>  = arrayListOf(),
  @SerializedName("name"                   ) var name                 : String?            = null,
  @SerializedName("release_date"           ) var releaseDate          : String?            = null,
  @SerializedName("release_date_precision" ) var releaseDatePrecision : String?            = null,
  @SerializedName("restrictions"           ) var restrictions         : Restrictions?      = Restrictions(),
  @SerializedName("type"                   ) var type                 : String?            = null,
  @SerializedName("uri"                    ) var uri                  : String?            = null,
  @SerializedName("artists"                ) var artists              : ArrayList<Artists> = arrayListOf(),

  )