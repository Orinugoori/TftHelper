package com.orinugoori.tfthelper

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface TFTApiService {
    // Data Dragon에서 TFT 증강 데이터 가져오기 (한국어)
    @GET("cdn/{version}/data/ko_KR/tft-augments.json")
    suspend fun getAugments(@Path("version") version: String): AugmentResponse

    // 최신 버전 정보 가져오기
    @GET("api/versions.json")
    suspend fun getVersions(): List<String>
}

// HTML 정리 및 설명 처리 함수들
fun cleanAugmentDescription(description: String): String {
    if (description.isBlank()) return "설명이 없습니다."
    
    var cleaned = description
    
    // HTML 태그 제거 (모든 태그 포함)
    cleaned = cleaned.replace(Regex("<[^>]*>"), "")
    
    // 특수 문자 및 플레이스홀더 제거
    cleaned = cleaned.replace(Regex("@[^@]*@"), "")
    cleaned = cleaned.replace(Regex("%[^%]*%"), "")
    cleaned = cleaned.replace("&nbsp;", " ")
    cleaned = cleaned.replace("&lt;", "<")
    cleaned = cleaned.replace("&gt;", ">")
    cleaned = cleaned.replace("&amp;", "&")
    cleaned = cleaned.replace("&quot;", "\"")
    cleaned = cleaned.replace("&#39;", "'")
    
    // 연속된 공백을 하나로 통합
    cleaned = cleaned.replace(Regex("\\s+"), " ")
    
    // 앞뒤 공백 제거
    cleaned = cleaned.trim()
    
    return if (cleaned.isBlank()) "설명이 없습니다." else cleaned
}

// API 응답에서 티어 추출 (ID 기반)
fun extractTierFromId(id: String): String {
    val lowerId = id.lowercase()
    
    return when {
        // 프리즘 티어 패턴
        lowerId.contains("iii") || 
        lowerId.contains("_3_") ||
        lowerId.contains("prismatic") ||
        lowerId.contains("legend") -> "프리즘"
        
        // 골드 티어 패턴  
        lowerId.contains("ii") || 
        lowerId.contains("_2_") ||
        lowerId.contains("gold") ||
        lowerId.contains("rare") -> "골드"
        
        // 실버 티어 (기본값)
        else -> "실버"
    }
}

// 증강 이름 정규화
fun normalizeAugmentName(name: String): String {
    return name.trim()
        .replace(Regex("\\s+"), " ")
        .replace("'", "'")
        .replace("'", "'")
        .replace(""", "\"")
        .replace(""", "\"")
}

// Data Dragon 응답을 앱에서 사용하는 형태로 변환
fun AugmentResponse.processAugments(): List<Augment> {
    return this.data.values.map { augment ->
        val cleanedName = normalizeAugmentName(augment.name)
        val tier = extractTierFromId(augment.id)
        val cleanedDescription = cleanAugmentDescription(augment.description ?: "")
        
        augment.copy(
            name = cleanedName,
            tier = tier,
            description = cleanedDescription
        )
    }.sortedBy { it.name }
}

// Retrofit 설정
object RetrofitInstance {
    private const val BASE_URL = "https://ddragon.leagueoflegends.com/"

    private val interceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val api: TFTApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TFTApiService::class.java)
    }
}

