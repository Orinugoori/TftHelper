package com.orinugoori.tfthelper.core.utils

import androidx.compose.ui.graphics.Brush
import com.orinugoori.tfthelper.core.constants.AppConstants
import com.orinugoori.tfthelper.core.constants.ColorConstants

/**
 * 티어 관련 유틸리티 함수들
 */
object TierUtils {
    
    /**
     * 티어에 따른 그라디언트 브러시 반환
     */
    fun getTierGradientBrush(tier: String): Brush {
        return when (tier) {
            AppConstants.AugmentTiers.SILVER -> {
                Brush.linearGradient(ColorConstants.Silver.ALL_GRADIENTS)
            }
            AppConstants.AugmentTiers.GOLD -> {
                Brush.linearGradient(ColorConstants.Gold.ALL_GRADIENTS)
            }
            AppConstants.AugmentTiers.PRISM -> {
                Brush.linearGradient(ColorConstants.Prism.ALL_GRADIENTS)
            }
            else -> {
                Brush.linearGradient(listOf(ColorConstants.WHITE, ColorConstants.GREY))
            }
        }
    }
    
    /**
     * 선택된 상태에 따른 그라디언트 브러시 반환
     */
    fun getSelectedTierBrush(tier: String, isSelected: Boolean): Brush {
        return if (isSelected) {
            getTierGradientBrush(tier)
        } else {
            Brush.linearGradient(listOf(ColorConstants.BLACK, ColorConstants.BLACK))
        }
    }
    
    /**
     * 티어에 따른 색상 반환 (배지용)
     */
    fun getTierColor(tier: String): androidx.compose.ui.graphics.Color {
        return when (tier) {
            AppConstants.AugmentTiers.SILVER -> ColorConstants.Silver.GRADIENT_3
            AppConstants.AugmentTiers.GOLD -> ColorConstants.Gold.GRADIENT_3
            AppConstants.AugmentTiers.PRISM -> ColorConstants.Prism.GRADIENT_3
            else -> ColorConstants.GREY
        }
    }
    
    /**
     * 이미지 파일명에서 티어 추출
     * 더 간단하고 읽기 쉬운 버전의 이미지 파일명 기반 티어 추출 함수
     */
    fun extractTierFromImageName(imageName: String): String {
        val cleanName = imageName.lowercase()
        
        // TFT_Set 패턴을 제거하여 세트 번호 간섭 방지
        val nameWithoutSet = cleanName.replace(Regex("tft_set\\d+"), "")
        
        return when {
            // 프리즘: 3 또는 III (세트 번호가 아닌 순수 티어 번호만)
            nameWithoutSet.contains("iii.") ||
                    nameWithoutSet.matches(Regex(".*[^\\d]3\\.(png|jpg|jpeg|webp)$")) ||
                    nameWithoutSet.matches(Regex(".*_3\\.(png|jpg|jpeg|webp)$")) -> AppConstants.AugmentTiers.PRISM
            
            // 골드: 2 또는 II (세트 번호가 아닌 순수 티어 번호만)
            nameWithoutSet.contains("ii.") ||
                    nameWithoutSet.matches(Regex(".*[^\\d]2\\.(png|jpg|jpeg|webp)$")) ||
                    nameWithoutSet.matches(Regex(".*_2\\.(png|jpg|jpeg|webp)$")) -> AppConstants.AugmentTiers.GOLD
            
            // 실버: 1 또는 I (세트 번호가 아닌 순수 티어 번호만)
            nameWithoutSet.contains("i.") ||
                    nameWithoutSet.matches(Regex(".*[^\\d]1\\.(png|jpg|jpeg|webp)$")) ||
                    nameWithoutSet.matches(Regex(".*_1\\.(png|jpg|jpeg|webp)$")) -> AppConstants.AugmentTiers.SILVER
            
            // 기본값: 실버
            else -> AppConstants.AugmentTiers.SILVER
        }
    }
}