package com.unex.musicgo.data.api.common

import com.google.gson.annotations.SerializedName


data class Owner (

    @SerializedName("external_urls" ) var externalUrls : ExternalUrls? = ExternalUrls(),
    @SerializedName("followers"     ) var followers    : Followers?    = Followers(),
    @SerializedName("href"          ) var href         : String?       = null,
    @SerializedName("id"            ) var id           : String?       = null,
    @SerializedName("type"          ) var type         : String?       = null,
    @SerializedName("uri"           ) var uri          : String?       = null,
    @SerializedName("display_name"  ) var displayName  : String?       = null

)