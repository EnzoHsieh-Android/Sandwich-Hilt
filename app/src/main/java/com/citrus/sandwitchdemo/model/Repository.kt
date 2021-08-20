package com.citrus.sandwitchdemo.model

import com.citrus.sandwitchdemo.api.ApiService
import com.citrus.sandwitchdemo.api.Resource
import com.skydoves.sandwich.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val apiService: ApiService) {

    fun getUsers() = flow {
        apiService.getUsersViaApiResponse()
            .suspendOnSuccess {
                emit(Resource.Success(data))
            }.suspendOnError {
                emit(Resource.Error(this.errorBody.toString()))
            }
    }.onCompletion { emit(Resource.Loading(false)) }
        .flowOn(IO)


    /** Merge - List型態專用，合併不同參數發送的結果
     *
     *  mergePolicy -
     *
     *    IGNORE_FAILURE：忽略失敗
     *    PREFERRED_FAILURE (default)： 可從responses取得失敗訊息
     * */
    fun getAlbumsById(currentPage:Int, nextPage:Int) = flow {
        apiService.getAlbumsViaApiResponse(currentPage).merge(
            apiService.getAlbumsViaApiResponse(nextPage),
            mergePolicy = ApiResponseMergePolicy.PREFERRED_FAILURE
        ).suspendOnSuccess {
            emit(Resource.Success(data))
        }.suspendOnError {
            emit(Resource.Error(this.errorBody.toString()))
        }
    }.onCompletion { emit(Resource.Loading(false)) }
        .flowOn(IO)

}