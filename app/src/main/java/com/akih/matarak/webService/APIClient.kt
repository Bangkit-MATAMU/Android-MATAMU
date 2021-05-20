package com.akih.matarak.webService

import com.akih.matarak.data.ModelArticle
import retrofit2.Call
import retrofit2.http.GET

interface APIClient {
    @GET("blog")
    fun getMovies(): Call<ModelArticle>
}