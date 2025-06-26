package com.orinugoori.tfthelper.data.remote.api

import com.orinugoori.tfthelper.data.model.AugmentResponseDto
import com.orinugoori.tfthelper.data.model.CommunityDragonAugmentDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * TFT API 서비스 인터페이스
 * Retrofit을 통해 외부 API와 통신하는 계층
 */
interface TFTApiService {
    
    /**
     * Data Dragon API에서 증강체 데이터를 가져오는 함수
     * @param version 게임 버전 (예: "15.1.1")
     * @return 증강체 응답 데이터
     */
    @GET("cdn/{version}/data/ko_KR/tft-augments.json")
    suspend fun getAugments(@Path("version") version: String): Response<AugmentResponseDto>
    
    /**
     * 최신 게임 버전 목록을 가져오는 함수
     * @return 버전 목록 (최신 버전이 첫 번째)
     */
    @GET("api/versions.json")
    suspend fun getVersions(): Response<List<String>>
    
    /**
     * Community Dragon API에서 증강체 데이터를 가져오는 함수
     * 더 상세한 설명과 메타데이터를 제공
     * @return Community Dragon 증강체 목록
     */
    @GET("latest/cdragon/tft/ko_kr.json")
    suspend fun getCommunityDragonAugments(): Response<Map<String, CommunityDragonAugmentDto>>
    
    /**
     * 특정 세트의 증강체 데이터를 가져오는 함수
     * @param setNumber TFT 세트 번호
     * @return 해당 세트의 증강체 데이터
     */
    @GET("cdn/latest/data/ko_KR/tft-augments-set{setNumber}.json")
    suspend fun getAugmentsBySet(@Path("setNumber") setNumber: Int): Response<AugmentResponseDto>
}

/**
 * Community Dragon API 서비스 인터페이스
 * 별도 베이스 URL을 사용하는 Community Dragon API 전용
 */
interface CommunityDragonApiService {
    
    /**
     * Community Dragon에서 TFT 증강체 데이터를 가져오는 함수
     * @return 증강체 맵 (ID -> 증강체 정보)
     */
    @GET("latest/cdragon/tft/ko_kr.json")
    suspend fun getTFTData(): Response<Map<String, Any>>
    
    /**
     * 특정 언어의 TFT 데이터를 가져오는 함수
     * @param locale 언어 코드 (예: "ko_kr", "en_us")
     * @return 해당 언어의 TFT 데이터
     */
    @GET("latest/cdragon/tft/{locale}.json")
    suspend fun getTFTDataByLocale(@Path("locale") locale: String): Response<Map<String, Any>>
}