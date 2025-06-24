package com.orinugoori.tfthelper

data class AugmentResponse(
    val data: Map<String, Augment>
)

data class Augment(
    val id: String,
    val tier : String = "",
    val name: String,
    val image: ImageInfo,
    val description : String = ""
)

data class ImageInfo(
    val full: String
)
