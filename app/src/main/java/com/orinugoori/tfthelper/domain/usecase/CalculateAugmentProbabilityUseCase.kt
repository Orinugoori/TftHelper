package com.orinugoori.tfthelper.domain.usecase

import com.orinugoori.tfthelper.data.model.ProbabilityResult
import com.orinugoori.tfthelper.core.constants.AppConstants

/**
 * TFT 증강체 확률 계산 UseCase
 * 게임 로직에 따른 확률 계산을 담당
 */
class CalculateAugmentProbabilityUseCase {
    
    /**
     * 첫 번째와 두 번째 증강체를 기반으로 세 번째 증강체 확률 계산
     * 
     * @param firstAugmentTier 첫 번째 선택된 증강체의 티어
     * @param secondAugmentTier 두 번째 선택된 증강체의 티어 (null 가능)
     * @return 각 티어별 확률 결과 리스트
     */
    fun calculateThirdAugmentProbability(
        firstAugmentTier: String?,
        secondAugmentTier: String?
    ): List<ProbabilityResult> {
        
        if (firstAugmentTier == null) {
            return getInitialProbabilities()
        }
        
        if (secondAugmentTier == null) {
            return getSecondAugmentProbabilities(firstAugmentTier)
        }
        
        return getThirdAugmentProbabilities(firstAugmentTier, secondAugmentTier)
    }
    
    /**
     * 첫 번째 증강체 선택 시 기본 확률
     */
    private fun getInitialProbabilities(): List<ProbabilityResult> {
        return listOf(
            ProbabilityResult(
                tier = AppConstants.AugmentTiers.SILVER,
                probability = 75,
                displayText = "실버 75%"
            ),
            ProbabilityResult(
                tier = AppConstants.AugmentTiers.GOLD,
                probability = 20,
                displayText = "골드 20%"
            ),
            ProbabilityResult(
                tier = AppConstants.AugmentTiers.PRISM,
                probability = 5,
                displayText = "프리즘 5%"
            )
        )
    }
    
    /**
     * 두 번째 증강체 확률 계산
     */
    private fun getSecondAugmentProbabilities(firstTier: String): List<ProbabilityResult> {
        return when (firstTier) {
            AppConstants.AugmentTiers.SILVER -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 55, "실버 55%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 35, "골드 35%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 10, "프리즘 10%")
            )
            AppConstants.AugmentTiers.GOLD -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 35, "실버 35%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 50, "골드 50%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 15, "프리즘 15%")
            )
            AppConstants.AugmentTiers.PRISM -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 25, "실버 25%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 50, "골드 50%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 25, "프리즘 25%")
            )
            else -> getInitialProbabilities()
        }
    }
    
    /**
     * 세 번째 증강체 확률 계산
     */
    private fun getThirdAugmentProbabilities(firstTier: String, secondTier: String): List<ProbabilityResult> {
        return when ("${firstTier}_${secondTier}") {
            "${AppConstants.AugmentTiers.SILVER}_${AppConstants.AugmentTiers.SILVER}" -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 40, "실버 40%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 45, "골드 45%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 15, "프리즘 15%")
            )
            "${AppConstants.AugmentTiers.SILVER}_${AppConstants.AugmentTiers.GOLD}" -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 30, "실버 30%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 50, "골드 50%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 20, "프리즘 20%")
            )
            "${AppConstants.AugmentTiers.SILVER}_${AppConstants.AugmentTiers.PRISM}" -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 20, "실버 20%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 50, "골드 50%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 30, "프리즘 30%")
            )
            "${AppConstants.AugmentTiers.GOLD}_${AppConstants.AugmentTiers.SILVER}" -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 30, "실버 30%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 50, "골드 50%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 20, "프리즘 20%")
            )
            "${AppConstants.AugmentTiers.GOLD}_${AppConstants.AugmentTiers.GOLD}" -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 25, "실버 25%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 50, "골드 50%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 25, "프리즘 25%")
            )
            "${AppConstants.AugmentTiers.GOLD}_${AppConstants.AugmentTiers.PRISM}" -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 15, "실버 15%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 50, "골드 50%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 35, "프리즘 35%")
            )
            "${AppConstants.AugmentTiers.PRISM}_${AppConstants.AugmentTiers.SILVER}" -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 20, "실버 20%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 50, "골드 50%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 30, "프리즘 30%")
            )
            "${AppConstants.AugmentTiers.PRISM}_${AppConstants.AugmentTiers.GOLD}" -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 15, "실버 15%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 50, "골드 50%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 35, "프리즘 35%")
            )
            "${AppConstants.AugmentTiers.PRISM}_${AppConstants.AugmentTiers.PRISM}" -> listOf(
                ProbabilityResult(AppConstants.AugmentTiers.SILVER, 10, "실버 10%"),
                ProbabilityResult(AppConstants.AugmentTiers.GOLD, 50, "골드 50%"),
                ProbabilityResult(AppConstants.AugmentTiers.PRISM, 40, "프리즘 40%")
            )
            else -> getInitialProbabilities()
        }
    }
}
