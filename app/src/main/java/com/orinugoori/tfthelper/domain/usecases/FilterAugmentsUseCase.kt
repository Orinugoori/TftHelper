package com.orinugoori.tfthelper.domain.usecases

import com.orinugoori.tfthelper.data.model.Augment
import com.orinugoori.tfthelper.util.cleanHtmlTags
import com.orinugoori.tfthelper.util.getInitialConsonants
import com.orinugoori.tfthelper.util.isInitialConsonantMatch
import com.orinugoori.tfthelper.util.normalizeAugmentName

class FilterAugmentsUseCase {

    operator fun invoke(query: String, tier: String, augments: List<Augment>): List<Augment> {
        val filteredByTier = if (tier == "전체") {
            augments
        } else {
            augments.filter { it.tier == tier }
        }

        return if (query.isBlank()) {
            filteredByTier
        } else {
            val normalizedQuery = normalizeAugmentName(query)
            val initialConsonantsQuery = getInitialConsonants(normalizedQuery)

            filteredByTier.filter { augment ->
                val normalizedAugmentName = normalizeAugmentName(augment.name)
                val cleanedDescription = cleanHtmlTags(augment.description)

                // 1. 일반 텍스트 포함 여부 (이름 또는 설명)
                val nameContainsQuery = normalizedAugmentName.contains(normalizedQuery) // ignoreCase는 normalizeName에서 처리
                val descContainsQuery = cleanedDescription.contains(normalizedQuery) // ignoreCase는 normalizeName에서 처리

                // 2. 초성 검색 (이름 또는 설명의 초성이 쿼리의 초성을 포함하는지)
                val nameInitialMatches = isInitialConsonantMatch(normalizedAugmentName, initialConsonantsQuery)
                val descInitialMatches = isInitialConsonantMatch(cleanedDescription, initialConsonantsQuery)

                nameContainsQuery || descContainsQuery || nameInitialMatches || descInitialMatches
            }
        }
    }
}