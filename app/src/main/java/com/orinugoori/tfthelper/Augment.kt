package com.orinugoori.tfthelper

import android.util.Log
import com.orinugoori.tfthelper.data.processor.AugmentProcessor

data class AugmentResponse(
    val data: Map<String, Augment>
)

data class Augment(
    val id: String,
    val tier : String = "",
    val name: String,
    val image: ImageInfo,
    val description : String = "",
    val keyword : List<String> = emptyList()
)

data class ImageInfo(
    val full: String
)

/**
 * 증강 데이터 처리 함수
 * 기존의 거대한 하드코딩된 맵 대신 자동 키워드 추출 시스템 사용
 * 
 * @param rawAugments API에서 받은 원본 증강 데이터
 * @return 키워드와 티어 정보가 추가된 처리된 증강 데이터
 */
fun processAugments(rawAugments: List<Augment>): List<Augment> {
    return AugmentProcessor.processAugments(rawAugments)
}
