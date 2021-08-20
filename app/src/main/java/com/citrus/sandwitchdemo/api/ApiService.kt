package com.citrus.sandwitchdemo.api

import com.citrus.sandwitchdemo.api.vo.Album
import com.citrus.sandwitchdemo.api.vo.User
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.*


interface ApiService {
    @GET("users")
    suspend fun getUsersViaApiResponse(): ApiResponse<List<User>>

    @GET("albums/{id}/photos")
    suspend fun getAlbumsViaApiResponse(@Path("id") id: Int): ApiResponse<List<Album>>
}