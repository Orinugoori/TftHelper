package com.orinugoori.tfthelper.data.model


// Data Dragon API 응답 구조
data class AugmentResponse(
    val type: String,
    val version: String,
    val data: Map<String, AugmentDto>
)

// API에서 받은 증강체 원본 데이터 (Data Transfer Object: DTO)
data class AugmentDto(
    val id: String,
    val name: String,
    val image: ImageInfo,
    val description: String // 원본 설명
)

/**
 * 이미지 정보
 */
data class ImageInfo(
    val full: String
)


/**
 * 증강 정보
 */
data class Augment(
    val id: String,
    val tier: String = "",
    val name: String,
    val imageUrl: String,
    val description: String
)






