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

/**
 * 더 간단하고 읽기 쉬운 버전의 이미지 파일명 기반 티어 추출 함수
 * 로마숫자(I, II, III)와 아라비아숫자(1, 2, 3) 모두 지원
 * TFT_Set13 같은 세트 정보는 무시하고 티어만 추출
 */
fun extractTierFromImageName(imageName: String): String {
    val cleanName = imageName.lowercase()

    // TFT_Set 패턴을 제거하여 세트 번호 간섭 방지
    val nameWithoutSet = cleanName.replace(Regex("tft_set\\d+"), "")

    return when {
        // 프리즘: 3 또는 III (세트 번호가 아닌 순수 티어 번호만)
        nameWithoutSet.contains("iii.") ||
                nameWithoutSet.matches(Regex(".*[^\\d]3\\.(png|jpg|jpeg|webp)$")) ||
                nameWithoutSet.matches(Regex(".*_3\\.(png|jpg|jpeg|webp)$")) -> "프리즘"

        // 골드: 2 또는 II (세트 번호가 아닌 순수 티어 번호만)
        nameWithoutSet.contains("ii.") ||
                nameWithoutSet.matches(Regex(".*[^\\d]2\\.(png|jpg|jpeg|webp)$")) ||
                nameWithoutSet.matches(Regex(".*_2\\.(png|jpg|jpeg|webp)$")) -> "골드"

        // 실버: 1 또는 I (세트 번호가 아닌 순수 티어 번호만)
        nameWithoutSet.contains("i.") ||
                nameWithoutSet.matches(Regex(".*[^\\d]1\\.(png|jpg|jpeg|webp)$")) ||
                nameWithoutSet.matches(Regex(".*_1\\.(png|jpg|jpeg|webp)$")) -> "실버"

        // 기본값: 실버
        else -> "실버"
    }
}


/**
 * API 응답의 증강체 데이터를 처리하여 올바른 티어 정보를 추가
 */
fun processAugmentData(response: AugmentResponse): List<Augment> {
    return response.data.values.map { augment ->
        val tier = extractTierFromImageName(augment.image.full)
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