package com.orinugoori.tfthelper.domain.usecase

import com.orinugoori.tfthelper.domain.model.AugmentProbability
import com.orinugoori.tfthelper.core.constants.AppConstants

/**
 * TFT 증강체 확률 계산을 담당하는 UseCase
 * 비즈니스 로직을 캡슐화하여 UI 레이어와 분리
 */
class ProbabilityCalculatorUseCase {
    
    /**
     * 첫 번째 증강체 선택 후 두 번째 증강체 확률을 계산하는 함수
     * @param firstSelected 첫 번째 선택한 증강체 티어
     * @return 정규화된 확률 리스트
     */
    fun calculateSecondProbabilities(firstSelected: String): List<AugmentProbability> {
        val baseSecondProbabilities = AppConstants.Probabilities.BASE_SECOND_PROBABILITIES
        val selectedProbability = baseSecondProbabilities[firstSelected] ?: 0
        val totalOthers = baseSecondProbabilities.values.sum() - selectedProbability
        
        return baseSecondProbabilities.map { (tier, probability) ->
            val adjustedProbability = if (tier == firstSelected) {
                0 // 같은 증강은 나올 수 없음
            } else {
                (probability * 100) / totalOthers
            }
            
            AugmentProbability(
                augmentName = tier,
                probability = adjustedProbability,
                tier = tier
            )
        }.filter { it.probability > 0 }
    }
    
    /**
     * 첫 번째, 두 번째 증강체 선택 후 세 번째 증강체 확률을 계산하는 함수
     * @param firstSelected 첫 번째 선택한 증강체 티어
     * @param secondSelected 두 번째 선택한 증강체 티어
     * @return 정규화된 확률 리스트
     */
    fun calculateThirdProbabilities(
        firstSelected: String,
        secondSelected: String
    ): List<AugmentProbability> {
        val baseThirdProbabilities = AppConstants.Probabilities.BASE_THIRD_PROBABILITIES
        val excludedTiers = setOf(firstSelected, secondSelected)
        
        val availableProbabilities = baseThirdProbabilities.filterKeys { tier ->
            tier !in excludedTiers
        }
        
        val totalRemaining = availableProbabilities.values.sum()
        
        return availableProbabilities.map { (tier, probability) ->
            val normalizedProbability = (probability * 100) / totalRemaining
            
            AugmentProbability(
                augmentName = tier,
                probability = normalizedProbability,
                tier = tier
            )
        }
    }
    
    /**
     * 증강체 선택 히스토리를 기반으로 다음 확률을 계산
     * @param selectedAugments 선택된 증강체들의 티어 리스트
     * @return 다음 선택 가능한 증강체들의 확률
     */
    fun calculateNextProbabilities(selectedAugments: List<String>): List<AugmentProbability> {
        return when (selectedAugments.size) {
            0 -> {
                // 첫 번째 선택: 기본 확률
                AppConstants.Probabilities.BASE_FIRST_PROBABILITIES.map { (tier, probability) ->
                    AugmentProbability(tier, probability, tier)
                }
            }
            1 -> {
                // 두 번째 선택
                calculateSecondProbabilities(selectedAugments[0])
            }
            2 -> {
                // 세 번째 선택
                calculateThirdProbabilities(selectedAugments[0], selectedAugments[1])
            }
            else -> {
                // 3개 이상 선택된 경우 빈 리스트 반환
                emptyList()
            }
        }
    }
}