package com.flickr.demo.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// ImageModel class for Image Retrieval from Flickr API
data class ImageModel(
    // Small Image URL
    @SerializedName("url_sq") val SmallImageURL: String,
    // Big Image URL
    @SerializedName("url_m") val BigImageURL: String,
    // Image Title / Description
    @SerializedName("title") val ImageDesc: String
) : Serializable