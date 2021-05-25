package com.akih.matarak.webservice

import com.akih.maps.model.NearbyPlaceResponse
import com.akih.matarak.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object NetworkModule {
    private var retrofit: Retrofit? = null
    private val OKHTTP_TIMEOUT = 60L

    private val okHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
        readTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
    }.build()

    private fun instanceRetrofit() : Retrofit {
        if(retrofit == null){
            return Retrofit.Builder().apply {
                baseUrl(BuildConfig.API_MAPS_URL)
                addConverterFactory(GsonConverterFactory.create())
                client(okHttpClient)
            }.build()
        }
        return retrofit!!
    }


    fun api() = instanceRetrofit().create(MapsApi::class.java)
}


interface MapsApi {
    @GET("nearbysearch/json")
    fun nearbyPlace(
        @Query("location") location: String,
//        @Query("type") type: String,
        @Query("radius") radius: String,
        @Query("keyword") keyword: String,
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY
    ) : Call<NearbyPlaceResponse>
}