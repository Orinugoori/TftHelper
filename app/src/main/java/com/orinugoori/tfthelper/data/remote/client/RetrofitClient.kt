package com.orinugoori.tfthelper.data.remote.client

import com.orinugoori.tfthelper.BuildConfig
import com.orinugoori.tfthelper.constants.AppConstants
import com.orinugoori.tfthelper.data.remote.api.TFTApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Retrofit 설정 - 올바른 Data Dragon URL 사용
object RetrofitInstance {

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
            .baseUrl(AppConstants.DATA_DRAGON_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TFTApiService::class.java)
    }
}

