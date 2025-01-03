package com.orinugoori.tfthelper


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface TFTApiService {
    @GET("cdn/14.24.1/data/ko_KR/tft-augments.json")
    suspend fun getAugments(): AugmentResponse
}

// Retrofit 설정
object RetrofitInstance {

    private const val BASE_URL = "https://ddragon.leagueoflegends.com/"

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 요청과 응답의 BODY 출력
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val api : TFTApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TFTApiService::class.java)
    }
}


