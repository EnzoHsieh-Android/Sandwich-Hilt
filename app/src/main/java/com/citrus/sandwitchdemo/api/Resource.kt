package com.citrus.sandwitchdemo.api



sealed class Resource <out T> (val status : Status, val _data : T?, val message: String?, val loading: Boolean?) {

    data class Success<out R>(val data : R) : Resource<R>(
        status = Status.SUCCESS,
        _data = data,
        message = null,
        loading = null
    )
    data class Loading(val isLoading : Boolean) : Resource<Nothing>(
        status = Status.LOADING,
        _data = null,
        message = null,
        loading = isLoading
    )
    data class Error(val exception: String) : Resource<Nothing>(
        status = Status.ERROR,
        _data = null,
        message = exception,
        loading = null
    )

}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}