package com.orinugoori.tfthelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.orinugoori.tfthelper.domain.usecase.ProbabilityCalculatorUseCase

/**
 * 확률 계산 뷰모델
 */
class ProbabilityViewModel : ViewModel() {
    
    private val probabilityCalculator = ProbabilityCalculatorUseCase()
    
    /**
     * 두 번째 증강 확률 계산
     */
    fun calculateSecondAugmentProbabilities(firstAugment: String): List<Pair<String, Int>> {
        return probabilityCalculator.calculateNormalizedSecondProbabilities(firstAugment)
    }
    
    /**
     * 두 번째 + 세 번째 증강 조합 확률 계산
     */
    fun calculateSecondThirdAugmentProbabilities(firstAugment: String): List<Triple<String, String, Int>> {
        return probabilityCalculator.calculateSecondThirdProbabilities(firstAugment)
    }
    
    /**
     * 세 번째 증강 확률 계산
     */
    fun calculateThirdAugmentProbabilities(
        firstAugment: String,
        secondAugment: String
    ): List<Pair<String, Int>> {
        return probabilityCalculator.calculateThirdProbabilities(firstAugment, secondAugment)
    }
}