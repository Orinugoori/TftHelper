package com.orinugoori.tfthelper.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orinugoori.tfthelper.constants.AppConstants // AppConstants 임포트

/**
 * 검색 기록을 관리하는 클래스 (SharedPreferences를 통해 저장 및 로드)
 * @param context 애플리케이션 컨텍스트
 */
class SearchHistoryManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(AppConstants.SEARCH_HISTORY_PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    // 타입을 MutableList<String>으로 변경하여 순서를 유지합니다.
    private val type = object : TypeToken<MutableList<String>>() {}.type

    /**
     * 현재 저장된 검색 기록을 가져옵니다.
     * @return 검색 기록 문자열 List (순서 유지)
     */
    fun getSearchHistory(): MutableList<String> {
        return try{
            val json = prefs.getString(AppConstants.KEY_SEARCH_HISTORY, null)
            if (json != null) {
                gson.fromJson(json, type) ?: mutableListOf()
            } else {
                // gson.fromJson이 null을 반환할 수 있으므로, null일 경우 빈 List 반환
                mutableListOf()
            }
        }catch (e : ClassCastException){
            Log.e("SearchHistoryManager", "ClassCastException: 이전 버전의 검색 기록을 String으로 읽으려 했으나 HashSet으로 저장되어 있습니다. 검색 기록을 초기화합니다.", e)
            clearSearchHistory() // 문제가 있는 데이터를 삭제
            mutableListOf() // 빈 목록 반환
        }catch (e : Exception){
            Log.e("SearchHistoryManager", "검색 기록을 불러오는 중 알 수 없는 오류 발생. 데이터를 초기화합니다.", e)
            clearSearchHistory()
            mutableListOf()
        }


    }

    /**
     * 새로운 검색어를 기록에 추가하고, 최대 개수를 초과하면 오래된 기록을 제거합니다.
     * @param query 추가할 검색어
     */
    fun addSearchQuery(query: String) {
        val currentHistory = getSearchHistory()

        // 1. 기존에 같은 검색어가 있다면 제거하여 최신순으로 정렬될 수 있도록 합니다.
        currentHistory.remove(query)

        // 2. 새로운 검색어를 리스트의 맨 뒤(가장 최근)에 추가합니다.
        currentHistory.add(query)

        // 3. 최대 개수(AppConstants.MAX_SEARCH_HISTORY_ITEMS)를 초과하는 경우,
        //    가장 오래된 검색어(리스트의 맨 앞)부터 제거합니다.
        while (currentHistory.size > AppConstants.MAX_SEARCH_HISTORY_ITEMS) {
            currentHistory.removeAt(0) // 맨 앞의 요소 제거
        }

        // 변경된 리스트를 저장합니다.
        saveSearchHistory(currentHistory)
    }

    /**
     * 특정 검색어를 기록에서 제거합니다.
     * @param query 제거할 검색어
     */
    fun removeSearchQuery(query: String) {
        val currentHistory = getSearchHistory()
        if (currentHistory.remove(query)) {
            saveSearchHistory(currentHistory)
        }
    }

    /**
     * 모든 검색 기록을 지웁니다.
     */
    fun clearSearchHistory() {
        prefs.edit().remove(AppConstants.KEY_SEARCH_HISTORY).apply()
    }

    /**
     * 검색 기록을 SharedPreferences에 저장합니다. (List를 JSON String으로 저장)
     * @param history 저장할 검색 기록 List
     */
    private fun saveSearchHistory(history: MutableList<String>) {
        val json = gson.toJson(history)
        prefs.edit().putString(AppConstants.KEY_SEARCH_HISTORY, json).apply() // putStringSet 대신 putString 사용
    }
}