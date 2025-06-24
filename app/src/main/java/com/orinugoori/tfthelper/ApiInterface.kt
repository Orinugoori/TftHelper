package com.orinugoori.tfthelper

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

// 기존 데이터 구조 유지 (호환성)
data class AugmentResponse(
    val data: Map<String, Augment>
)

data class Augment(
    val id: String,
    val tier: String = "",
    val name: String,
    val image: ImageInfo,
    val description: String = ""
)

data class ImageInfo(
    val full: String
)

/**
 * 증강체 이름을 기반으로 티어를 정확히 분류하는 함수
 * TFT 증강체는 이름의 패턴으로 티어를 구분함
 */
fun extractTierFromAugmentName(name: String): String {
    val cleanName = name.trim()
    
    return when {
        // 프리즘 티어 패턴들
        cleanName.endsWith(" III") -> "프리즘"
        cleanName.endsWith(" Crown") -> "프리즘" 
        cleanName.endsWith(" Heart") -> "프리즘"
        cleanName.endsWith(" Soul") -> "프리즘"
        
        // 골드 티어 패턴들
        cleanName.endsWith(" II") -> "골드"
        cleanName.endsWith(" Crest") -> "골드"
        
        // 실버 티어 패턴들 (기본값)
        cleanName.endsWith(" I") -> "실버"
        else -> "실버" // 숫자나 접미사가 없으면 실버
    }
}

/**
 * API 응답의 증강체 데이터를 처리하여 올바른 티어 정보를 추가
 */
fun processAugmentData(response: AugmentResponse): List<Augment> {
    return response.data.values.map { augment ->
        val tier = extractTierFromAugmentName(augment.name)
        val cleanedDescription = cleanHtmlTags(augment.description)
        
        augment.copy(
            tier = tier,
            description = cleanedDescription
        )
    }.sortedBy { it.name }
}

/**
 * HTML 태그 및 특수 문자를 정리하는 함수
 */
fun cleanHtmlTags(description: String): String {
    if (description.isBlank()) return "설명이 없습니다."
    
    var cleaned = description
    
    // HTML 태그 제거 (모든 종류의 태그)
    cleaned = cleaned.replace(Regex("<[^>]*>"), "")
    
    // TFT에서 자주 사용되는 플레이스홀더 제거
    cleaned = cleaned.replace(Regex("@[^@]*@"), "")
    cleaned = cleaned.replace(Regex("%[^%]*%"), "")
    cleaned = cleaned.replace(Regex("\\{\\{[^}]*\\}\\}"), "")
    
    // HTML 엔티티 변환
    cleaned = cleaned.replace("&nbsp;", " ")
    cleaned = cleaned.replace("&lt;", "<")
    cleaned = cleaned.replace("&gt;", ">")
    cleaned = cleaned.replace("&amp;", "&")
    cleaned = cleaned.replace("&quot;", "\"")
    cleaned = cleaned.replace("&#x27;", "'")
    
    // 연속된 공백을 하나로 통합
    cleaned = cleaned.replace(Regex("\\s+"), " ")
    
    // 앞뒤 공백 제거
    cleaned = cleaned.trim()
    
    return if (cleaned.isBlank()) "설명이 없습니다." else cleaned
}

/**
 * 증강체 이름을 정규화하는 함수
 */
fun normalizeAugmentName(name: String): String {
    return name.trim()
        .replace(Regex("\\s+"), " ")
        .replace("'", "'")
        .replace("'", "'")
        .replace(""", "\"")
        .replace(""", "\"")
}

// Retrofit 설정 - 올바른 Data Dragon URL 사용
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