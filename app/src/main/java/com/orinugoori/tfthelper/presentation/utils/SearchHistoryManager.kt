package com.orinugoori.tfthelper.presentation.utils

import android.content.Context
import android.content.SharedPreferences
import com.orinugoori.tfthelper.core.constants.AppConstants

/**
 * 검색 기록 관리자
 */
class SearchHistoryManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        AppConstants.Cache.SEARCH_HISTORY_PREFS,
        Context.MODE_PRIVATE
    )
    
    /**
     * 검색 기록에 추가
     */
    fun addToHistory(query: String) {
        val currentHistory = getHistory().toMutableList()
        
        // 중복 제거
        currentHistory.remove(query)
        // 맨 앞에 추가
        currentHistory.add(0, query)
        // 최대 10개까지만 저장
        if (currentHistory.size > AppConstants.Cache.MAX_SEARCH_HISTORY) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        
        saveHistory(currentHistory)
    }
    
    /**
     * 검색 기록 가져오기
     */
    fun getHistory(): List<String> {
        val historySet = prefs.getStringSet("history", emptySet()) ?: emptySet()
        return historySet.toList().take(AppConstants.Cache.MAX_SEARCH_HISTORY)
    }
    
    /**
     * 검색 기록 삭제
     */
    fun clearHistory() {
        prefs.edit().clear().apply()
    }
    
    /**
     * 검색 기록 저장
     */
    private fun saveHistory(history: List<String>) {
        prefs.edit()
            .putStringSet("history", history.toSet())
            .apply()
    }
}