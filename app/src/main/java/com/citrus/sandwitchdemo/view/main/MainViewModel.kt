package com.citrus.sandwitchdemo.view.main

import android.util.Log
import androidx.lifecycle.*
import com.citrus.sandwitchdemo.api.*
import com.citrus.sandwitchdemo.api.vo.Album
import com.citrus.sandwitchdemo.api.vo.User
import com.citrus.sandwitchdemo.model.Repository
import com.skydoves.sandwich.*
import com.skydoves.sandwich.disposables.CompositeDisposable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    /**適合Repository Pattern的寫法*/
    private val model: Repository,
    /**不透過Repository的寫法*/
    private val apiService: ApiService,
    private val dataSourceService: DataSourceService
) : ViewModel() {

    /** --------以ApiResponse封裝類型的api處理方式---------*/

    /**sharedFlow 單次觸發，效果同singleLiveEvent*/
    private val _albumFlow = MutableSharedFlow<Resource<List<Album>>>()
    val albumFlow: SharedFlow<Resource<List<Album>>> = _albumFlow

    /**stateFlow 有資料就觸發，效果同LiveData，不丟失連續emit的value*/
    private val _userFlow = MutableStateFlow<Resource<List<User>>>(Resource.Loading(true))
    val userFlow: StateFlow<Resource<List<User>>> = _userFlow

    /**Repository Pattern*/
    /**collect Repository 執行結果，處理資料後emit給View*/
    fun fetchUsers() {
        viewModelScope.launch {
            model.getUsers().collect { data ->
                _userFlow.emit(data)
            }
        }
    }

    /**Repository Pattern*/
    /**同上，提供merge兩支回傳類型相同api的list合併*/
    fun fetchAlbums() =
        viewModelScope.launch {
            model.getAlbumsById(currentPage = 1, nextPage = 2).collect { result ->
                _albumFlow.emit(result)
            }
        }


    /**在viewModel直接接收api資料轉換成liveData*/
    /**success若要資料處理寫在toLiveData{ }內*/
    val posterListLiveData: LiveData<Resource<List<User>>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(
                apiService.getUsersViaApiResponse()
                    .suspendOnError {
                        emit(Resource.Error(this.errorBody.toString()))
                        // handles error cases when the API request gets an error response.
                    }.suspendOnException {
                        emit(Resource.Error(this.message!!))
                        // handles exceptional cases when the API request gets an exception response.
                    }.toLiveData {
                        /**包裝成Resource回傳*/
                        return@toLiveData Resource.Success(this)
                    }
            )
        }


    /** --------以DataSource封裝類型的api處理方式---------*/
    val albumLiveData = MutableLiveData<Resource<List<Album>>>()
    val userLiveData = MutableLiveData<List<User>>()
    private val disposable = CompositeDisposable()

    /**non Repository*/
    private fun getAlbumsViaDataSource() = dataSourceService.getAlbumsViaDataSource(1)
        /**retry fetching data 3 times with 5000L interval when the request gets failure.*/
        .retry(3, 5000L)
        /**這行不寫不能用*/
        .joinDisposable(disposable)
        .observeResponse { response ->
            response.onSuccess {
                albumLiveData.value = Resource.Success(this.data!!)
            }.onError {
                albumLiveData.value = Resource.Error(this.errorBody.toString())
            }.onException {
                albumLiveData.value = Resource.Error(this.message!!)
            }
        }

    /**non Repository*/
    private fun getUsersViaDataSource() = dataSourceService.getUsersViaDataSource()
        .retry(3, 5000L)
        .joinDisposable(disposable)
        .observeResponse { response -> // handle the case when the API request gets a success response.
            response.onSuccess {
                userLiveData.value = data!!
            }.onError {
                Log.e("data", "error")
            }.onException {
                Log.e("data", "exception")
            }
        }


    /**DataSource需要request()啟動*/
    fun getStuff() {
        getAlbumsViaDataSource()
            .request()
            /**request Success後順序執行concat內容*/
            .concat(getUsersViaDataSource())
    }


    /**必要清除*/
    override fun onCleared() {
        super.onCleared()
        if (!disposable.disposed) {
            disposable.clear()
        }
    }

}