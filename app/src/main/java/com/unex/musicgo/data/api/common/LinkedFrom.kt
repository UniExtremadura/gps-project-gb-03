package com.unex.musicgo.data.api.common

import com.google.gson.annotations.SerializedName


data class LinkedFrom (

    @SerializedName("external_urls" ) var externalUrls : ExternalUrls? = ExternalUrls(),
    @SerializedName("href"          ) var href         : String?       = null,
    @SerializedName("id"            ) var id           : String?       = null,
    @SerializedName("type"          ) var type         : String?       = null,
    @SerializedName("uri"           ) var uri          : String?       = null

)