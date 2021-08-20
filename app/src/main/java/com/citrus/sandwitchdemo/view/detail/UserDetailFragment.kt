package com.citrus.sandwitchdemo.view.detail

import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.citrus.sandwitchdemo.api.Resource
import com.citrus.sandwitchdemo.databinding.FragmentUserDetailBinding
import com.citrus.sandwitchdemo.util.base.BindingFragment
import com.citrus.sandwitchdemo.view.detail.adapter.AlbumAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class UserDetailFragment : BindingFragment<FragmentUserDetailBinding>() {
    private val viewModel: DetailViewModel by viewModels()
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentUserDetailBinding::inflate

    @Inject
    lateinit var albumAdapter: AlbumAdapter


    override fun initView() {
        binding.albumRv.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = albumAdapter
        }
    }

    override fun initAction() {
        viewModel.fetchAlbums()
    }

    override fun initObserve() {
        lifecycleScope.launchWhenStarted {
            viewModel.albumFlow.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        albumAdapter.updateDataset(resource.data)
                    }
                    is Resource.Error -> {
                        Log.e("albums", resource.message!!)
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }
}