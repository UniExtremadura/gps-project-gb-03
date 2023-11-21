package com.unex.musicgo.data.api.common

import com.google.gson.annotations.SerializedName

data class Items (

  @SerializedName("authors"           ) var authors          : ArrayList<Authors>    = arrayListOf(),
  @SerializedName("available_markets" ) var availableMarkets : ArrayList<String>     = arrayListOf(),
  @SerializedName("copyrights"        ) var copyrights       : ArrayList<Copyrights> = arrayListOf(),
  @SerializedName("description"       ) var description      : String?               = null,
  @SerializedName("html_description"  ) var htmlDescription  : String?               = null,
  @SerializedName("edition"           ) var edition          : String?               = null,
  @SerializedName("explicit"          ) var explicit         : Boolean?              = null,
  @SerializedName("external_urls"     ) var externalUrls     : ExternalUrls?         = ExternalUrls(),
  @SerializedName("href"              ) var href             : String?               = null,
  @SerializedName("id"                ) var id               : String?               = null,
  @SerializedName("images"            ) var images           : ArrayList<Images>     = arrayListOf(),
  @SerializedName("languages"         ) var languages        : ArrayList<String>     = arrayListOf(),
  @SerializedName("media_type"        ) var mediaType        : String?               = null,
  @SerializedName("name"              ) var name             : String?               = null,
  @SerializedName("narrators"         ) var narrators        : ArrayList<Narrators>  = arrayListOf(),
  @SerializedName("publisher"         ) var publisher        : String?               = null,
  @SerializedName("type"              ) var type             : String?               = null,
  @SerializedName("uri"               ) var uri              : String?               = null,
  @SerializedName("total_chapters"    ) var totalChapters    : Int?                  = null,
  @SerializedName("preview_url"       ) var previewUrl       : String?               = null,
  @SerializedName("album"             ) var album            : Album?                = Album(),
  @SerializedName("artists"           ) var artists          : ArrayList<Artists>    = arrayListOf(),
  @SerializedName("genres"            ) var genres           : ArrayList<String>     = arrayListOf(),
  @SerializedName("disc_number"       ) var discNumber       : Int?                  = null,
  @SerializedName("duration_ms"       ) var durationMs       : Int?                  = null,
  @SerializedName("external_ids"      ) var externalIds      : ExternalIds?          = ExternalIds(),
  @SerializedName("is_playable"       ) var isPlayable       : Boolean?              = null,
  @SerializedName("linked_from"       ) var linkedFrom       : LinkedFrom?           = LinkedFrom(),
  @SerializedName("restrictions"      ) var restrictions     : Restrictions?         = Restrictions(),
  @SerializedName("popularity"        ) var popularity       : Int?                  = null,
  @SerializedName("track_number"      ) var trackNumber      : Int?                  = null,
  @SerializedName("is_local"          ) var isLocal          : Boolean?              = null

)