package com.orinugoori.tfthelper.util

import android.util.Log
import com.orinugoori.tfthelper.data.model.AugmentResponse

object DebugUtils {

    /**
     * API 응답에서 증강체 이름과 이미지 파일명을 로그로 출력하는 함수
     */
    fun logAugmentData(response: AugmentResponse) {
        Log.d("TFT_API_DEBUG", "=== 증강체 API 응답 데이터 분석 ===")
        Log.d("TFT_API_DEBUG", "총 증강체 개수: ${response.data.size}")
        Log.d("TFT_API_DEBUG", "----------------------------------------")

        response.data.values.forEachIndexed { index, augment ->
            Log.d("TFT_API_DEBUG", "[$index] 이름: ${augment.name}")
            Log.d("TFT_API_DEBUG", "[$index] 이미지: ${augment.image.full}")
            Log.d(
                "TFT_API_DEBUG",
                "[$index] 이미지 기반 티어: ${extractTierFromImageName(augment.image.full)}"
            )
            Log.d("TFT_API_DEBUG", "----------------------------------------")
        }

        Log.d("TFT_API_DEBUG", "=== 로그 출력 완료 ===")
    }

    /**
     * 티어별로 그룹화해서 로그 출력하는 함수
     */
    fun logAugmentDataGrouped(response: AugmentResponse) {
        Log.d("TFT_API_DEBUG", "=== 티어별 증강체 분석 ===")

        val augmentsByTier = response.data.values.groupBy { augment ->
            extractTierFromImageName(augment.name)
        }

        augmentsByTier.forEach { (tier, augments) ->
            Log.d("TFT_API_DEBUG", "🎯 $tier 티어 (${augments.size}개)")
            augments.forEach { augment ->
                Log.d("TFT_API_DEBUG", "   • ${augment.name} → ${augment.image.full}")
            }
            Log.d("TFT_API_DEBUG", "")
        }

        Log.d("TFT_API_DEBUG", "=== 티어별 분석 완료 ===")
    }

    /**
     * 특정 패턴의 증강체들만 필터링해서 로그 출력
     */
    fun logSpecificPatterns(response: AugmentResponse) {
        Log.d("TFT_API_DEBUG", "=== 특정 패턴 증강체 분석 ===")

        val augments = response.data.values

        // I, II, III 패턴 찾기
        val romanNumeralAugments = augments.filter { augment ->
            augment.name.contains(" I") || augment.name.contains(" II") || augment.name.contains(" III")
        }

        Log.d("TFT_API_DEBUG", "🔍 로마숫자 패턴 증강체 (${romanNumeralAugments.size}개)")
        romanNumeralAugments.forEach { augment ->
            Log.d("TFT_API_DEBUG", "   • ${augment.name} → ${augment.image.full}")
        }

        // 숫자 패턴 찾기 (1, 2, 3)
        val numberPatternAugments = augments.filter { augment ->
            augment.image.full.contains("1.") || augment.image.full.contains("2.") || augment.image.full.contains(
                "3."
            )
        }

        Log.d("TFT_API_DEBUG", "🔢 숫자 패턴 이미지 증강체 (${numberPatternAugments.size}개)")
        numberPatternAugments.forEach { augment ->
            Log.d("TFT_API_DEBUG", "   • ${augment.name} → ${augment.image.full}")
        }

        // 패턴이 없는 증강체들
        val noPatternAugments = augments.filter { augment ->
            !augment.name.contains(" I") && !augment.name.contains(" II") && !augment.name.contains(
                " III"
            ) &&
                    !augment.image.full.contains("1.") && !augment.image.full.contains("2.") && !augment.image.full.contains(
                "3."
            )
        }

        Log.d("TFT_API_DEBUG", "❓ 패턴 없는 증강체 (${noPatternAugments.size}개)")
        noPatternAugments.forEach { augment ->
            Log.d("TFT_API_DEBUG", "   • ${augment.name} → ${augment.image.full}")
        }

        Log.d("TFT_API_DEBUG", "=== 패턴 분석 완료 ===")
    }


// AugmentRepository.kt에 추가할 디버깅용 함수들

    /**
     * API 응답 데이터를 상세히 분석하고 로그로 출력하는 함수
     * 개발 중에만 사용하고, 릴리즈 전에 제거할 것
     */
    fun analyzeApiDataForDebugging(response: AugmentResponse) {
        Log.d("TFT_API_DEBUG", "=== TFT 증강체 API 데이터 분석 시작 ===")
        Log.d("TFT_API_DEBUG", "총 증강체 개수: ${response.data.size}")

        // 1. ID 패턴 분석
        analyzeIdPatterns(response)

        // 2. 이미지 파일명 패턴 분석
        analyzeImagePatterns(response)

        // 3. 설명 내용 분석
        analyzeDescriptions(response)

        // 4. 이름 패턴 분석
        analyzeNamePatterns(response)

        Log.d("TFT_API_DEBUG", "=== TFT 증강체 API 데이터 분석 완료 ===")
    }

    /**
     * 증강체 ID 패턴 분석
     */
    fun analyzeIdPatterns(response: AugmentResponse) {
        Log.d("TFT_API_DEBUG", "\n=== ID 패턴 분석 ===")

        val searchPatterns = listOf(
            "revival",
            "tutorial",
            "tft_set10",
            "tft10",
            "test",
            "debug",
            "temp",
            "placeholder"
        )

        searchPatterns.forEach { pattern ->
            val matchingAugments = response.data.values.filter {
                it.id.lowercase().contains(pattern)
            }

            if (matchingAugments.isNotEmpty()) {
                Log.d("TFT_API_DEBUG", "🔍 '$pattern' 패턴 발견 (${matchingAugments.size}개):")
                matchingAugments.forEach { augment ->
                    Log.d("TFT_API_DEBUG", "  - ID: ${augment.id}")
                    Log.d("TFT_API_DEBUG", "    이름: ${augment.name}")
                    Log.d("TFT_API_DEBUG", "    이미지: ${augment.image.full}")
                }
            } else {
                Log.d("TFT_API_DEBUG", "❌ '$pattern' 패턴: 발견되지 않음")
            }
        }
    }

    /**
     * 이미지 파일명 패턴 분석
     */
    fun analyzeImagePatterns(response: AugmentResponse) {
        Log.d("TFT_API_DEBUG", "\n=== 이미지 파일명 패턴 분석 ===")

        val imagePatterns = listOf("revival", "tutorial", "tft_set10", "set10", "test", "debug")

        imagePatterns.forEach { pattern ->
            val matchingAugments = response.data.values.filter {
                it.image.full.lowercase().contains(pattern)
            }

            if (matchingAugments.isNotEmpty()) {
                Log.d("TFT_API_DEBUG", "🖼️ 이미지에서 '$pattern' 패턴 발견 (${matchingAugments.size}개):")
                matchingAugments.forEach { augment ->
                    Log.d("TFT_API_DEBUG", "  - 이름: ${augment.name}")
                    Log.d("TFT_API_DEBUG", "    이미지: ${augment.image.full}")
                }
            } else {
                Log.d("TFT_API_DEBUG", "❌ 이미지에서 '$pattern' 패턴: 발견되지 않음")
            }
        }
    }

    /**
     * 증강체 설명 내용 분석
     */
    fun analyzeDescriptions(response: AugmentResponse) {
        Log.d("TFT_API_DEBUG", "\n=== 설명 내용 분석 ===")

        val descriptionKeywords = listOf(
            "튜토리얼", "tutorial",
            "테스트", "test",
            "임시", "temp", "temporary",
            "디버그", "debug",
            "개발자", "developer",
            "내부 테스트", "internal test",
            "플레이스홀더", "placeholder"
        )

        descriptionKeywords.forEach { keyword ->
            val matchingAugments = response.data.values.filter {
                it.description.lowercase().contains(keyword.lowercase())
            }

            if (matchingAugments.isNotEmpty()) {
                Log.d("TFT_API_DEBUG", "📝 설명에서 '$keyword' 키워드 발견 (${matchingAugments.size}개):")
                matchingAugments.forEach { augment ->
                    Log.d("TFT_API_DEBUG", "  - 이름: ${augment.name}")
                    Log.d("TFT_API_DEBUG", "    설명: ${augment.description.take(100)}...")
                }
            } else {
                Log.d("TFT_API_DEBUG", "❌ 설명에서 '$keyword' 키워드: 발견되지 않음")
            }
        }
    }

    /**
     * 증강체 이름 패턴 분석
     */
    fun analyzeNamePatterns(response: AugmentResponse) {
        Log.d("TFT_API_DEBUG", "\n=== 이름 패턴 분석 ===")

        // 특수 문자나 패턴이 있는 이름들 찾기
        val specialPatterns = listOf("TFT_", "DEBUG_", "TEST_", "[", "]", "(개발)", "(테스트)", "(임시)")

        specialPatterns.forEach { pattern ->
            val matchingAugments = response.data.values.filter {
                it.name.contains(pattern)
            }

            if (matchingAugments.isNotEmpty()) {
                Log.d("TFT_API_DEBUG", "🏷️ 이름에서 '$pattern' 패턴 발견 (${matchingAugments.size}개):")
                matchingAugments.forEach { augment ->
                    Log.d("TFT_API_DEBUG", "  - 이름: ${augment.name}")
                }
            } else {
                Log.d("TFT_API_DEBUG", "❌ 이름에서 '$pattern' 패턴: 발견되지 않음")
            }
        }
    }

    /**
     * 현재 블랙리스트에 있는 증강체들이 실제로 API에 존재하는지 확인
     */
    fun verifyBlacklistedAugments(response: AugmentResponse) {
        Log.d("TFT_API_DEBUG", "\n=== 블랙리스트 검증 ===")

        val blacklistedNames = setOf(
            // Revival 관련
            "광란의 축제", "넋을 빼놓는 공연", "밴드 전원 집합",
            "보호막 강타", "레트로 게이머", "랩의 여왕", "파티 개시자",

            // Tutorial 관련
            "방벽 - 이상현상", "확산지대 - 이상현상", "정예 선봉대", "재빠른 공격",

            // 알 수 없는 것들
            "매각", "사이버네틱 이식술 1", "사이버네틱 집합체",

            // 10시즌 관련 (일부만 샘플로)
            "감정의 유대", "거물중의 거물", "대마 불사", "더블 펑크", "동전 투입"
        )

        blacklistedNames.forEach { blacklistedName ->
            val found = response.data.values.find { it.name == blacklistedName }
            if (found != null) {
                Log.d("TFT_API_DEBUG", "✅ 블랙리스트 검증: '$blacklistedName' 발견")
                Log.d("TFT_API_DEBUG", "    ID: ${found.id}")
                Log.d("TFT_API_DEBUG", "    이미지: ${found.image.full}")
            } else {
                Log.d("TFT_API_DEBUG", "❌ 블랙리스트 검증: '$blacklistedName' 미발견 (이미 제거되었거나 이름이 변경됨)")
            }
        }

    }
}