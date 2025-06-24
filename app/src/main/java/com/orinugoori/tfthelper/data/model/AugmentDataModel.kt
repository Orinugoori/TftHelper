package com.orinugoori.tfthelper.data.model

/**
 * 증강 데이터 모델
 * API에서 받은 기본 데이터에 추가 정보를 매핑하기 위한 구조
 */
data class AugmentData(
    val description: String,
    val keywords: List<String>
)

/**
 * 증강 데이터 생성을 위한 헬퍼 함수
 * 더 간결하고 읽기 쉬운 데이터 정의를 위해 사용
 * 
 * @param description 증강 설명
 * @param keywords 키워드 목록 (가변 인자)
 * @return AugmentData 객체
 */
fun augmentData(description: String, vararg keywords: String): AugmentData = 
    AugmentData(description, keywords.toList())

/**
 * 증강 응답 데이터 (API에서 받는 원본 데이터)
 */
data class AugmentResponse(
    val data: Map<String, Augment>
)

/**
 * 증강 정보
 */
data class Augment(
    val id: String,
    val tier: String = "",
    val name: String,
    val image: ImageInfo,
    val description: String = "",
    val keyword: List<String> = emptyList()
)

/**
 * 이미지 정보
 */
data class ImageInfo(
    val full: String
)
