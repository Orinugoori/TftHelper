package com.orinugoori.tfthelper

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AugmentViewModel : ViewModel() {
    private val _augments = MutableStateFlow<List<Augment>>(emptyList())
    val augments: StateFlow<List<Augment>> = _augments

    private val _filteredAugments = MutableStateFlow<List<Augment>>(emptyList())
    val filteredAugments: StateFlow<List<Augment>> = _filteredAugments

    private val _keywordList = MutableStateFlow<Set<String>>(emptySet())
    val keywordList: StateFlow<Set<String>> = _keywordList


    private val _selectedTier = MutableStateFlow("전체")
    val selectedTier: StateFlow<String> = _selectedTier

    private val _selectedKeyword = MutableStateFlow("전체")
    val selectedKeyword: StateFlow<String> = _selectedKeyword


    init {
        fetchAugments()
    }

    //서버에서 데이터 가져오기
    private fun fetchAugments() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAugments()

                Log.d("API Response", "Augments : ${response.data}")

                val gson = Gson()
                val jsonResponse = gson.toJson(response)
                Log.d("Gson Debug", "Json Response: $jsonResponse")

                val augmentResponse = gson.fromJson(jsonResponse, AugmentResponse::class.java)
                Log.d("Gson Debug", "Parsed Augments: $augmentResponse")

                val rawAugments = response.data.values.toList()
                loadAugments(rawAugments)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("API Error", "Failed to fetch or parse response", e)
            }
        }
    }

    //증강 데이터에 증강 설명 추가
    private fun loadAugments(rawAugments: List<Augment>) {
        val processedAugments = processAugments(rawAugments)
        _augments.value = processedAugments
        _filteredAugments.value = processedAugments
        loadKeywordList(processedAugments)
    }


    private fun loadKeywordList(augments: List<Augment>) {
        val keywords =
            augments.flatMap { it.keyword }.toSet().toMutableList().apply { add(0, "전체") }.toSet()
        _keywordList.value = keywords
    }

    fun filterAugmentsByTier(tier: String) {
        applyFilters(tier = tier)
    }


    fun filterAugmentsByKeyword(keyword: String) {
        applyFilters(keyword = keyword)
    }

    private fun applyFilters(tier: String? = null, keyword: String? = null) {

        if (tier != null) _selectedTier.value = tier
        if (keyword != null) _selectedKeyword.value = keyword

        _filteredAugments.value = _augments.value.filter { augment ->
            (_selectedTier.value == "전체" || augment.tier == _selectedTier.value) &&
                    (_selectedKeyword.value == "전체" || augment.keyword.contains(_selectedKeyword.value))
        }

    }
}


