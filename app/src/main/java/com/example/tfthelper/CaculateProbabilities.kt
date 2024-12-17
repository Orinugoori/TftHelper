package com.example.tfthelper

class CaculateProbabilities {

    fun calculateNormalizedSecondProbabilities(firstSelected: String): List<Pair<String, Int>> {
        val filteredData = augmentProbabilityList.filter { it.firstAug == firstSelected }

        val groupedProbabilities = filteredData.groupBy { it.secondAug }
            .mapValues { entry ->
                entry.value.sumOf { it.probability }
            }

        val totalProbability = groupedProbabilities.values.sum()

        // 정규화 후 확률 기준으로 내림차순 정렬
        return groupedProbabilities.map { (secondAug, probability) ->
            Pair(secondAug, ((probability.toDouble() / totalProbability) * 100).toInt())
        }.sortedByDescending { it.second } // 확률 기준으로 내림차순 정렬
    }


    // 첫 번째 증강 선택 → 두 번째 + 세 번째 조합 확률 계산 (정렬된 리스트 반환)
    fun calculateSecondThirdProbabilities(firstSelected: String): List<Triple<String, String, Int>> {
        // 첫 번째 증강이 일치하는 데이터만 필터링
        val filteredData = augmentProbabilityList.filter { it.firstAug == firstSelected }

        // 두 번째 + 세 번째 증강을 Pair로 그룹화하고 확률 합산
        val groupedProbabilities = filteredData.groupBy { Pair(it.secondAug, it.thirdAug) }
            .mapValues { entry ->
                entry.value.sumOf { it.probability }
            }

        // 전체 확률 합산
        val totalProbability = groupedProbabilities.values.sum()

        // 정규화 및 정렬
        return groupedProbabilities.map { (pair, probability) ->
            Triple(
                pair.first,
                pair.second,
                ((probability.toDouble() / totalProbability) * 100).toInt()
            )
        }.sortedByDescending { it.third } // 확률 기준으로 내림차순 정렬
    }

    fun calculateThirdProbabilities(firstSelected: String, secondSelected: String): List<Pair<String, Int>> {
        // 첫 번째와 두 번째 증강이 일치하는 데이터만 필터링
        val filteredData = augmentProbabilityList.filter {
            it.firstAug == firstSelected && it.secondAug == secondSelected
        }

        // 세 번째 증강을 기준으로 그룹화하고 확률 합산
        val groupedProbabilities = filteredData.groupBy { it.thirdAug }
            .mapValues { entry ->
                entry.value.sumOf { it.probability }
            }

        // 전체 확률 합산
        val totalProbability = groupedProbabilities.values.sum()

        // 정규화 후 확률 기준으로 정렬
        return groupedProbabilities.map { (thirdAug, probability) ->
            Pair(thirdAug, ((probability.toDouble() / totalProbability) * 100).toInt())
        }.sortedByDescending { it.second } // 확률 기준으로 내림차순 정렬
    }


}

