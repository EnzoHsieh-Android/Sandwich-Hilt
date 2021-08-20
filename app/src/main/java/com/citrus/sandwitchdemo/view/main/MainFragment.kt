package com.citrus.sandwitchdemo.view.main

import android.util.Log
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.citrus.sandwitchdemo.R
import com.citrus.sandwitchdemo.api.Resource
import com.citrus.sandwitchdemo.databinding.FragmentMainBinding
import com.citrus.sandwitchdemo.util.base.BindingFragment
import com.citrus.sandwitchdemo.view.main.adapter.UserAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : BindingFragment<FragmentMainBinding>() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
     lateinit var userAdapter: UserAdapter

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMainBinding::inflate


    override fun initView() {
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = userAdapter
        }

        userAdapter.setOnItemClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_userDetailFragment)
        }
    }

    override fun initAction() {
        viewModel.fetchUsers()
        viewModel.fetchAlbums()
        viewModel.getStuff()
    }

    override fun initObserve() {
        viewModel.albumLiveData.observe(viewLifecycleOwner, {
            Log.e("dataSource album:", it._data?.size.toString())
        })

        viewModel.userLiveData.observe(viewLifecycleOwner, {
            Log.e("dataSource user:", it.size.toString())
        })

        viewModel.posterListLiveData.observe(viewLifecycleOwner, { resource ->
            when(resource) {
                is Resource.Success -> {
                    Log.e("liveData",resource._data.toString())
                }
                is Resource.Error -> {
                    Log.e("liveData",resource.message.toString())
                }
                is  Resource.Loading -> {
                    binding.progressCircular.isVisible = resource.isLoading
                }
            }
        })

        lifecycleScope.launchWhenStarted {
            viewModel.albumFlow.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Log.e(
                            "albums id1",
                            resource._data?.filter { it.albumId == 1 }?.size.toString()
                        )
                        Log.e(
                            "albums id2",
                            resource._data?.filter { it.albumId == 2 }?.size.toString()
                        )
                    }
                    is Resource.Error -> {
                        Log.e("albums", resource.message!!)
                    }
                    is Resource.Loading -> {
                        binding.progressCircular.isVisible = resource.isLoading
                    }
                }

            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.userFlow.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource._data?.let {
                            userAdapter.updateDataset(it)
                        }
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),resource.message!!,Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressCircular.isVisible = resource.isLoading
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        /**
         * because DI didn't release adapter until onDestroy ,
         * before that if onDestroyView happened and recyclerView still hold adapter will lead memory leak
         * */
        binding.rvUsers.adapter = null
        super.onDestroyView()
    }
}