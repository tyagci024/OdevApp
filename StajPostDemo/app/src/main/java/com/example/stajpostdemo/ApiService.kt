package com.example.stajpostdemo

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("/ask")
    suspend fun createPost(@Body postData: PostData): Response<ResponseData>
}
