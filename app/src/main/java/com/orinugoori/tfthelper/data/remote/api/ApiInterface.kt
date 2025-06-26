package com.orinugoori.tfthelper.data.remote.api

import com.orinugoori.tfthelper.data.model.AugmentResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TFTApiService {
    // 올바른 Data Dragon TFT 증강 API
    @GET("cdn/{version}/data/ko_KR/tft-augments.json")
    suspend fun getAugments(@Path("version") version: String): AugmentResponse

    // 최신 버전 정보 가져오기
    @GET("api/versions.json")
    suspend fun getVersions(): List<String>
}



