package com.citrus.sandwitchdemo.api


import com.citrus.sandwitchdemo.api.vo.Album
import com.citrus.sandwitchdemo.api.vo.User
import com.skydoves.sandwich.DataSource
import retrofit2.http.*


interface DataSourceService {
    @GET("users")
    fun getUsersViaDataSource(): DataSource<List<User>>

    @GET("albums/{id}/photos")
    fun getAlbumsViaDataSource(@Path("id") id: Int) : DataSource<List<Album>>
}