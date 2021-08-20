package com.citrus.sandwitchdemo.di

import androidx.fragment.app.Fragment
import com.citrus.sandwitchdemo.api.ApiService
import com.citrus.sandwitchdemo.api.DataSourceService
import com.citrus.sandwitchdemo.util.Constants
import com.citrus.sandwitchdemo.view.detail.adapter.AlbumAdapter
import com.citrus.sandwitchdemo.view.main.adapter.UserAdapter
import com.skydoves.sandwich.DataSourceCallAdapterFactory
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    companion object {
        private const val DEFAULT_CONNECT_TIME = 10L
        private const val DEFAULT_WRITE_TIME = 30L
        private const val DEFAULT_READ_TIME = 30L

        @Provides
        @Singleton
        fun okHttpClient(): OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)
                .build()


        @Provides
        @Singleton
        @ApiResponseRetrofit
        fun provideRetrofitByApiResponse(okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        @Provides
        @Singleton
        @DataSourceRetrofit
        fun provideRetrofitByDataSource(okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(DataSourceCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()


        @Provides
        @Singleton
        fun provideApiService(@ApiResponseRetrofit retrofit: Retrofit): ApiService =
            retrofit.create(ApiService::class.java)

        @Provides
        @Singleton
        fun provideDataSourceService(@DataSourceRetrofit retrofit: Retrofit): DataSourceService =
            retrofit.create(DataSourceService::class.java)


    }
}

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

    @Provides
    fun provideUserAdapter(fragment: Fragment):UserAdapter {
        return UserAdapter(fragment.requireContext())
    }

    @Provides
    fun provideAlbumAdapter(fragment: Fragment): AlbumAdapter {
        return AlbumAdapter(fragment.requireContext())
    }

}


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiResponseRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DataSourceRetrofit