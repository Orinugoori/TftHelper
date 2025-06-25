package com.orinugoori.tfthelper

// Data Dragon API 응답 구조
data class AugmentResponse(
    val type: String,
    val version: String,
    val data: Map<String, Augment>
)

// TFT 증강체 데이터 클래스
data class Augment(
    val id: String,
    val name: String,
    val tier: String = "",
    val image: ImageInfo,
    val description: String
)

// 이미지 정보 클래스
data class ImageInfo(
    val full: String
)

