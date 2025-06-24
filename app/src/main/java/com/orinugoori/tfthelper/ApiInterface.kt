package com.orinugoori.tfthelper

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface TFTApiService {
    // Community Dragon에서 TFT 증강 데이터 가져오기 (한국어)
    @GET("latest/cdragon/tft/ko_kr.json")
    suspend fun getAugments(): CommunityDragonResponse

    // 버전 정보는 Data Dragon에서 가져오기 (호환성 유지)
    @GET("api/versions.json")
    suspend fun getVersions(): List<String>
}

// Community Dragon 응답 데이터 구조
data class CommunityDragonResponse(
    val sets: Map<String, TftSet>
)

data class TftSet(
    val name: String,
    val augments: List<CommunityDragonAugment>?
)

data class CommunityDragonAugment(
    val apiName: String,
    val name: String,
    val desc: String,
    val icon: String
)

// 기존 호환성을 위한 변환 함수들
fun CommunityDragonResponse.toAugmentResponse(): AugmentResponse {
    val augments = mutableMapOf<String, Augment>()
    
    // 모든 세트에서 증강 데이터 추출
    sets.values.forEach { set ->
        set.augments?.forEach { cdAugment ->
            augments[cdAugment.apiName] = Augment(
                id = cdAugment.apiName,
                name = cdAugment.name,
                description = cdAugment.desc,
                tier = extractTierFromApiName(cdAugment.apiName),
                image = ImageInfo(
                    full = cdAugment.icon.substringAfterLast('/')
                )
            )
        }
    }
    
    return AugmentResponse(data = augments)
}

// API 이름에서 티어 추출 (기존 로직 유지)
private fun extractTierFromApiName(apiName: String): String {
    return when {
        apiName.contains("III", ignoreCase = true) || 
        apiName.contains("_3_") -> "프리즘"
        apiName.contains("II", ignoreCase = true) || 
        apiName.contains("_2_") -> "골드"
        else -> "실버"
    }
}

// 향후 확장을 위한 추가 엔드포인트들 (Data Dragon 사용)
interface DataDragonApiService {
    @GET("cdn/{version}/data/ko_KR/tft-champion.json")
    suspend fun getChampions(@Path("version") version: String): ChampionResponse

    @GET("cdn/{version}/data/ko_KR/tft-trait.json")
    suspend fun getTraits(@Path("version") version: String): TraitResponse

    @GET("cdn/{version}/data/ko_KR/tft-item.json")
    suspend fun getItems(@Path("version") version: String): ItemResponse
}

// 새로운 응답 데이터 클래스들 (향후 확장용)
data class ChampionResponse(
    val data: Map<String, Champion>
)

data class TraitResponse(
    val data: Map<String, Trait>
)

data class ItemResponse(
    val data: Map<String, Item>
)

// 향후 확장을 위한 데이터 클래스들
data class Champion(
    val id: String,
    val name: String,
    val tier: Int,
    val image: ImageInfo
)

data class Trait(
    val id: String,
    val name: String,
    val description: String
)

data class Item(
    val id: String,
    val name: String,
    val image: ImageInfo
)

// Retrofit 설정 - Community Dragon과 Data Dragon 분리
object RetrofitInstance {
    // Community Dragon - TFT 증강 데이터용
    private const val COMMUNITY_DRAGON_BASE_URL = "https://raw.communitydragon.org/"
    
    // Data Dragon - 버전 정보 및 기타 데이터용
    private const val DATA_DRAGON_BASE_URL = "https://ddragon.leagueoflegends.com/"

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

    // Community Dragon API (증강 데이터)
    val api: TFTApiService by lazy {
        Retrofit.Builder()
            .baseUrl(COMMUNITY_DRAGON_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TFTApiService::class.java)
    }

    // Data Dragon API (버전 정보)
    val dataDragonApi: DataDragonApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DATA_DRAGON_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DataDragonApiService::class.java)
    }
}
