package com.orinugoori.tfthelper.core.extensions

import java.util.Locale

/**
 * 문자열 확장 함수들
 */

/**
 * 한글 초성 검색을 위한 함수
 */
fun String.getInitialConsonants(): String {
    val initialConsonants = arrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )
    
    return this.map { char ->
        if (char in '가'..'힣') {
            val index = (char.code - '가'.code) / 588
            initialConsonants[index]
        } else {
            char
        }
    }.joinToString("")
}

/**
 * 검색어 매칭 함수 (초성 검색 지원)
 */
fun String.matchesSearch(query: String): Boolean {
    if (query.isEmpty()) return true
    
    val lowercaseQuery = query.lowercase(Locale.KOREAN)
    val lowercaseText = this.lowercase(Locale.KOREAN)
    
    // 일반 검색
    if (lowercaseText.contains(lowercaseQuery)) return true
    
    // 초성 검색
    val initialConsonants = this.getInitialConsonants()
    return initialConsonants.contains(lowercaseQuery)
}

/**
 * 안전한 문자열 변환
 */
fun String?.orEmpty(): String = this ?: ""

/**
 * 문자열이 숫자인지 확인
 */
fun String.isNumeric(): Boolean {
    return this.toDoubleOrNull() != null
}
