package com.orinugoori.tfthelper.core.extensions

/**
 * 한글 초성을 추출하는 확장 함수
 * TFT 증강체 검색에서 초성 검색을 지원하기 위해 사용
 */
fun String.getChosung(): String {
    val chosungList = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )
    
    val result = StringBuilder()
    
    for (char in this) {
        if (char in '가'..'힣') {
            val index = (char.code - '가'.code) / 588
            result.append(chosungList[index])
        } else {
            result.append(char)
        }
    }
    
    return result.toString()
}

/**
 * 문자열이 특정 초성과 매치되는지 확인
 */
fun String.matchesChosung(query: String): Boolean {
    return this.getChosung().contains(query, ignoreCase = true) || 
           this.contains(query, ignoreCase = true)
}

/**
 * 티어 이름을 정규화하는 확장 함수
 */
fun String.normalizeTier(): String {
    return when (this.lowercase()) {
        "silver", "실버" -> "실버"
        "gold", "골드" -> "골드"
        "prism", "prismatic", "프리즘" -> "프리즘"
        else -> this
    }
}

/**
 * 증강체 이름에서 불필요한 접미사 제거
 */
fun String.cleanAugmentName(): String {
    return this.replace(Regex("\\s+(I{1,3})$"), "")
        .replace(Regex("\\s+\\d+$"), "")
        .trim()
}