package com.orinugoori

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

var hashMapping : Map<String,String> = emptyMap()

interface HashApi {
    @GET("hashes/lol/hashes.binhashes.txt") // GitHub의 해시 데이터 URL
    fun getItemHashes(): Call<String>
}


object HashRetrofitInstance{
    private const val BASE_URL = "https://raw.githubusercontent.com/CommunityDragon/Data/master/"

    private val client = OkHttpClient.Builder()
        .build()

    val api : HashApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .build()
            .create(HashApi::class.java)
    }
}



fun fetchHashMapping(context: Context, onComplete: (Map<String, String>) -> Unit) {
    HashRetrofitInstance.api.getItemHashes().enqueue(object : Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                val rawData = response.body() ?: ""
                hashMapping = parseHashMapping(rawData)
                onComplete(hashMapping)
            } else {
                println("Hash API Error: ${response.code()} - ${response.message()}")
            }
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            println("Hash API Network Error: ${t.message}")
        }
    })
}

fun parseHashMapping(rawData: String) : Map<String, String>{
    return rawData.lines()
        .filter { it.contains("=") }
        .associate {
            val (key, value) = it.split("=")
            key.trim() to value.trim()
        }
}