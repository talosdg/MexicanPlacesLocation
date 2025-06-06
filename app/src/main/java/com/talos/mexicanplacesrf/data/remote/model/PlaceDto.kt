package com.talos.mexicanplacesrf.data.remote.model

import android.R
import com.google.gson.annotations.SerializedName

data class PlaceDto(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("thumbnail")
    var thumbnail: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("state")
    var state: String? = null,
    @SerializedName("update")
    var update: String? = null,
    @SerializedName("attractions")
    var attractions: String? = null
)
