package com.example.tfthelper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AugmentViewModel : ViewModel() {
    private val _augments = MutableStateFlow<List<Augment>>(emptyList())
    val augments : StateFlow<List<Augment>> = _augments

    private val _filteredAugments = MutableStateFlow<List<Augment>>(emptyList())
    val filteredAugments : StateFlow<List<Augment>> = _filteredAugments

    init {
        fetchAugments()
    }

    //서버에서 데이터 가져오기
    private fun fetchAugments(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAugments()
                val rawAugments = response.data.values.toList()
                loadAugments(rawAugments)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    //증강 데이터에 증강 설명 추가
    fun loadAugments(rawAugments : List<Augment>){
        val processedAugments = processAugments(rawAugments)
        _augments.value = processedAugments
        _filteredAugments.value = processedAugments
    }

    fun filterAugments(tier : String){
        val filteredList = if(tier == "전체"){
            _augments.value
        }else{
            filterAugmentsByTier(augments.value, tier)
        }
        _filteredAugments.value = filteredList
    }


}