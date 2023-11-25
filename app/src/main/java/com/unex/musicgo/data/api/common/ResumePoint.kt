package com.unex.musicgo.data.api.common

import com.google.gson.annotations.SerializedName


data class ResumePoint (

  @SerializedName("fully_played"       ) var fullyPlayed      : Boolean? = null,
  @SerializedName("resume_position_ms" ) var resumePositionMs : Int?     = null

)