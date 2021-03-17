package com.tailoredapps.codagram.ui.homeFeedScreen


import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.FilterGroupItemBinding
import com.tailoredapps.codagram.databinding.SearchDetailPageBinding
import com.tailoredapps.codagram.models.Group
import com.tailoredapps.codagram.models.User
import com.tailoredapps.codagram.ui.groupscreen.GroupDetailsAdapter
import timber.log.Timber

class FilterGroupAdapter : ListAdapter<Group, GroupScreenViewHolder>(
    DiffCallback()
) {
    lateinit var mItemCLicked: ItemFilterListener

    class DiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupScreenViewHolder {
        return GroupScreenViewHolder(
            FilterGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: GroupScreenViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            holder.apply {
                mItemCLicked.let {
                    mItemCLicked.onItemClicked(getItem(position))
                }
            }
        }
    }

    fun setUpListener(itemCLicked: ItemFilterListener) {
        mItemCLicked = itemCLicked
    }

    interface ItemFilterListener {
        fun onItemClicked(group: Group)
    }
}

class GroupScreenViewHolder(private val binding: FilterGroupItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.N)
    fun bind(postData: Group) {
        Glide.with(itemView)
            .load(postData.image?.url)
            .placeholder(R.drawable.ic_baseline_image_48)
            .into(binding.groupImage)

        binding.tvGroupName.text = postData.name.toString()
        binding.tvCreatorName.text = postData.creator?.lastname.toString()
    }
}
