package com.citrus.sandwitchdemo.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.sandwitchdemo.api.Resource
import com.citrus.sandwitchdemo.api.vo.Album
import com.citrus.sandwitchdemo.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
/**Hilt setup 07 - 應用inject的viewModel須加上@HiltViewModel*/
class DetailViewModel @Inject constructor(
    private val model: Repository,
): ViewModel() {

    private val _albumFlow = MutableSharedFlow<Resource<List<Album>>>()
    val albumFlow: SharedFlow<Resource<List<Album>>> = _albumFlow


    fun fetchAlbums() =
        viewModelScope.launch {
            model.getAlbumsById(currentPage = 1, nextPage = 2).collect { result ->
                _albumFlow.emit(result)
            }
        }

}