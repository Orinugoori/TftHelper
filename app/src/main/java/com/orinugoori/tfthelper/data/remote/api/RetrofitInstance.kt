package com.orinugoori.tfthelper.data.remote.api

import com.orinugoori.tfthelper.core.constants.AppConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Retrofit 인스턴스를 관리하는 싱글톤 객체
 * API 통신을 위한 설정을 중앙화
 */
object RetrofitInstance {
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    /**
     * Data Dragon API용 Retrofit 인스턴스
     */
    private val dataDragonRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.API.DATA_DRAGON_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Community Dragon API용 Retrofit 인스턴스
     */
    private val communityDragonRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.API.COMMUNITY_DRAGON_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Data Dragon API 서비스 인스턴스
     */
    val dataDragonApi: TFTApiService by lazy {
        dataDragonRetrofit.create(TFTApiService::class.java)
    }
    
    /**
     * Community Dragon API 서비스 인스턴스
     */
    val communityDragonApi: CommunityDragonApiService by lazy {
        communityDragonRetrofit.create(CommunityDragonApiService::class.java)
    }
    
    /**
     * 사용자 정의 설정으로 API 서비스를 생성하는 함수
     * @param baseUrl 기본 URL
     * @param serviceClass 서비스 클래스
     * @return 생성된 API 서비스
     */
    fun <T> createApiService(baseUrl: String, serviceClass: Class<T>): T {
        val customRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return customRetrofit.create(serviceClass)
    }
}