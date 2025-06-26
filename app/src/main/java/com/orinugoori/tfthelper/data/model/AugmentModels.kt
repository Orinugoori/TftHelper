package com.orinugoori.tfthelper.data.model

/**
 * Data Dragon API 응답 구조
 */
data class AugmentResponse(
    val type: String,
    val version: String,
    val data: Map<String, Augment>
)

/**
 * TFT 증강체 데이터 클래스
 */
data class Augment(
    val id: String,
    val name: String,
    val tier: String = "",
    val image: ImageInfo,
    val description: String
)

/**
 * 이미지 정보 클래스
 */
data class ImageInfo(
    val full: String
)

/**
 * Community Dragon API 응답 구조
 */
data class CommunityDragonAugment(
    val apiName: String,
    val name: String,
    val desc: String,
    val icon: String
)

/**
 * 버전 응답 구조
 */
data class VersionResponse(
    val versions: List<String>
)

/**
 * UI에서 사용하는 증강체 모델
 */
data class UiAugment(
    val id: String,
    val name: String,
    val tier: String,
    val imageUrl: String,
    val description: String,
    val searchableText: String = "${name} ${description}".lowercase()
)

/**
 * 확률 계산 결과 모델
 */
data class ProbabilityResult(
    val tier: String,
    val probability: Int,
    val displayText: String
)
