package com.citrus.sandwitchdemo.view.detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.citrus.sandwitchdemo.api.vo.Album
import com.citrus.sandwitchdemo.databinding.ItemUserBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumAdapter @Inject constructor(val context: Context):RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
    var albums = listOf<Album>()

    class AlbumViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    suspend fun updateDataset(newDataset: List<Album>) =
        withContext(Dispatchers.Default) {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return albums.size
                }

                override fun getNewListSize(): Int {
                    return newDataset.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return albums[oldItemPosition] == newDataset[newItemPosition]
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return albums[oldItemPosition] == newDataset[newItemPosition]
                }
            })
            withContext(Dispatchers.Main) {
                albums = newDataset
                diff.dispatchUpdatesTo(this@AlbumAdapter)
            }
        }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val data = albums[position]
        holder.binding.apply {
            username.text = data.title
            userId.text = data.albumId.toString()
            describe.text = data.url
        }
    }
}