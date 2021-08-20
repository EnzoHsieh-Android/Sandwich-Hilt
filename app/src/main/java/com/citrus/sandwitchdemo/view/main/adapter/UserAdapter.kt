package com.citrus.sandwitchdemo.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.citrus.sandwitchdemo.api.vo.User
import com.citrus.sandwitchdemo.databinding.ItemUserBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class UserAdapter @Inject constructor(val context: Context):RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    var users = listOf<User>()

    class UserViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    suspend fun updateDataset(newDataset: List<User>) =
        withContext(Dispatchers.Default) {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return users.size
                }

                override fun getNewListSize(): Int {
                    return newDataset.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return users[oldItemPosition] == newDataset[newItemPosition]
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return users[oldItemPosition] == newDataset[newItemPosition]
                }
            })
            withContext(Dispatchers.Main) {
                users = newDataset
                diff.dispatchUpdatesTo(this@UserAdapter)
            }
        }


    override fun getItemCount(): Int {
        return users.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val data = users[position]
        holder.binding.apply {
            username.text = data.username
            userId.text = data.id.toString()
            describe.text = data.email

            root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click()
                }
            }
        }
    }


    private var onItemClickListener: (() -> Unit)? = null
    fun setOnItemClickListener(listener: () -> Unit) {
        onItemClickListener = listener
    }

}