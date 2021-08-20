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
    /**Repository Pattern*/
    private val model: Repository,
    /**non Repository*/
    private val apiService: ApiService,
    private val dataSourceService: DataSourceService
) : ViewModel() {

    val albumLiveData = MutableLiveData<Resource<List<Album>>>()
    val userLiveData = MutableLiveData<List<User>>()
    private val disposable = CompositeDisposable()

    private val _albumFlow = MutableSharedFlow<Resource<List<Album>>>()
    val albumFlow: SharedFlow<Resource<List<Album>>> = _albumFlow

    private val _userFlow = MutableStateFlow<Resource<List<User>>>(Resource.Loading(true))
    val userFlow: StateFlow<Resource<List<User>>> = _userFlow

    /**Repository Pattern*/
    /**接收api資料轉換成flow*/
    fun fetchUsers() {
        viewModelScope.launch {
            model.getUsers().collect { data ->
                _userFlow.emit(data)
            }
        }
    }

    /**Repository Pattern*/
    fun fetchAlbums() =
        viewModelScope.launch {
            model.getAlbumsById(currentPage = 1, nextPage = 2).collect { result ->
                _albumFlow.emit(result)
            }
        }


    /**non Repository*/
    /**接收api資料轉換成liveData*/
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
                        return@toLiveData Resource.Success(this)
                    }
            ) // returns an observable LiveData
        }


    /**non Repository*/
    private fun getAlbumsViaDataSource() = dataSourceService.getAlbumsViaDataSource(1)
        // retry fetching data 3 times with 5000L interval when the request gets failure.
        .retry(3, 5000L)
        /**這行不寫不能用*/
        .joinDisposable(disposable)
        .observeResponse { response -> // handle the case when the API request gets a success response.
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

    /**invoke DataSource*/
    fun getStuff() {
        getAlbumsViaDataSource()
            .request()
            .concat(getUsersViaDataSource())
    }


    override fun onCleared() {
        super.onCleared()
        if (!disposable.disposed) {
            disposable.clear()
        }
    }

}