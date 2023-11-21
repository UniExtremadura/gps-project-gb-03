package com.unex.musicgo.data.api.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token") var accessToken: String? = null,
    @SerializedName("token_type") var tokenType: String? = null,
    @SerializedName("expires_in") var expiresIn: Int? = null
)