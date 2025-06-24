package com.orinugoori.tfthelper

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface TFTApiService {
    // 최신 버전 정보 가져오기
    @GET("api/versions.json")
    suspend fun getVersions(): List<String>

    // 동적 버전으로 증강 데이터 가져오기
    @GET("cdn/{version}/data/ko_KR/tft-augments.json")
    suspend fun getAugments(@Path("version") version: String): AugmentResponse

    // 향후 확장을 위한 추가 엔드포인트들
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

// Retrofit 설정 - 개선됨
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