package com.orinugoori.tfthelper.data.model

import com.orinugoori.tfthelper.domain.model.Augment

/**
 * API에서 받아오는 증강체 응답 데이터
 * Data Dragon API의 실제 응답 구조를 반영
 */
data class AugmentResponseDto(
    val type: String,
    val version: String,
    val data: Map<String, AugmentDto>
)

/**
 * API에서 받아오는 개별 증강체 데이터
 */
data class AugmentDto(
    val id: String,
    val name: String,
    val image: ImageInfoDto,
    val description: String = ""
)

/**
 * 이미지 정보 DTO
 */
data class ImageInfoDto(
    val full: String
)

/**
 * Community Dragon API 응답 데이터
 */
data class CommunityDragonAugmentDto(
    val apiName: String,
    val name: String,
    val desc: String,
    val icon: String
)

/**
 * DTO를 Domain Model로 변환하는 확장 함수들
 */
fun AugmentDto.toDomain(tier: String = ""): Augment {
    return Augment(
        id = this.id,
        name = this.name,
        tier = tier,
        description = this.description,
        imageUrl = this.image.full,
        keywords = extractKeywords(this.description)
    )
}

fun CommunityDragonAugmentDto.toDomain(tier: String = ""): Augment {
    return Augment(
        id = this.apiName,
        name = this.name,
        tier = tier,
        description = this.desc,
        imageUrl = this.icon,
        keywords = extractKeywords(this.desc)
    )
}

/**
 * 설명에서 키워드를 추출하는 헬퍼 함수
 */
private fun extractKeywords(description: String): List<String> {
    // 간단한 키워드 추출 로직
    // 실제로는 더 정교한 로직이 필요할 수 있음
    return description.split(" ", ",", ".", "!")
        .map { it.trim() }
        .filter { it.length > 2 }
        .distinct()
        .take(5) // 최대 5개의 키워드만 추출
}