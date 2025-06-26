package com.orinugoori.tfthelper.data.remote.api

import com.orinugoori.tfthelper.data.model.AugmentResponse
import com.orinugoori.tfthelper.data.model.CommunityDragonAugment
import com.orinugoori.tfthelper.data.model.VersionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * TFT API 인터페이스
 * Data Dragon과 Community Dragon API를 통합 관리
 */
interface TftApiService {
    
    /**
     * 최신 게임 버전 정보 가져오기
     */
    @GET("api/versions.json")
    suspend fun getVersions(): Response<List<String>>
    
    /**
     * 증강체 데이터 가져오기 (Data Dragon)
     */
    @GET("cdn/{version}/data/{language}/tft-augments.json")
    suspend fun getAugments(
        @Path("version") version: String,
        @Path("language") language: String = "ko_KR"
    ): Response<AugmentResponse>
    
    /**
     * Community Dragon에서 증강체 데이터 가져오기
     */
    @GET("tft/augments.json")
    suspend fun getCommunityDragonAugments(): Response<List<CommunityDragonAugment>>
    
    /**
     * 특정 세트의 증강체 데이터 가져오기
     */
    @GET("cdn/{version}/data/{language}/tft-augments.json")
    suspend fun getAugmentsBySet(
        @Path("version") version: String,
        @Path("language") language: String = "ko_KR"
    ): Response<AugmentResponse>
}
