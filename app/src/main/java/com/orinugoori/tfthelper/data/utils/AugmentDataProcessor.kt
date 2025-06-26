package com.orinugoori.tfthelper.data.utils

import com.orinugoori.tfthelper.core.utils.TierUtils
import com.orinugoori.tfthelper.domain.model.Augment
import com.orinugoori.tfthelper.domain.model.AugmentResponse

/**
 * 증강체 데이터 처리 유틸리티
 */
object AugmentDataProcessor {
    
    /**
     * API 응답의 증강체 데이터를 처리하여 올바른 티어 정보를 추가
     */
    fun processAugmentData(response: AugmentResponse): List<Augment> {
        return response.data.values.map { augment ->
            val tier = TierUtils.extractTierFromImageName(augment.image.full)
            val cleanedDescription = cleanHtmlTags(augment.description)
            
            augment.copy(
                tier = tier,
                description = cleanedDescription
            )
        }.sortedBy { it.name }
    }
    
    /**
     * HTML 태그 및 특수 문자를 정리하는 함수
     */
    private fun cleanHtmlTags(description: String): String {
        if (description.isBlank()) return "설명이 없습니다."
        
        var cleaned = description
        
        // HTML 태그 제거 (모든 종류의 태그)
        cleaned = cleaned.replace(Regex("<[^>]*>"), "")
        
        // TFT에서 자주 사용되는 플레이스홀더 제거
        cleaned = cleaned.replace(Regex("@[^@]*@"), "")
        cleaned = cleaned.replace(Regex("%[^%]*%"), "")
        cleaned = cleaned.replace(Regex("\\{\\{[^}]*\\}\\}"), "")
        
        // HTML 엔티티 변환
        cleaned = cleaned.replace("&nbsp;", " ")
        cleaned = cleaned.replace("&lt;", "<")
        cleaned = cleaned.replace("&gt;", ">")
        cleaned = cleaned.replace("&amp;", "&")
        cleaned = cleaned.replace("&quot;", "\"")
        cleaned = cleaned.replace("&#x27;", "'")
        
        // 연속된 공백을 하나로 통합
        cleaned = cleaned.replace(Regex("\\s+"), " ")
        
        // 앞뒤 공백 제거
        cleaned = cleaned.trim()
        
        return if (cleaned.isBlank()) "설명이 없습니다." else cleaned
    }
    
    /**
     * 증강체 이름을 정규화하는 함수
     */
    fun normalizeAugmentName(name: String): String {
        return name.trim()
            .replace(Regex("\\s+"), " ")
            .replace("'", "'")
            .replace("'", "'")
            .replace("\u201c", "\"")
            .replace("\u201d", "\"")
    }
}