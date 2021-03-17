package com.tailoredapps.codagram.ui.groupscreen

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.GroupscreenMygroupsBinding
import com.tailoredapps.codagram.models.Group
import com.tailoredapps.codagram.ui.homeFeedScreen.firstViewModule
import timber.log.Timber

class GroupAdapter :
    ListAdapter<Group, GroupScreenViewHolder>(object : DiffUtil.ItemCallback<Group>() {

        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean =
            oldItem.creator?.id == newItem.creator?.id


        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean =
            oldItem == newItem

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupScreenViewHolder {
        return GroupScreenViewHolder(
            GroupscreenMygroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: GroupScreenViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class GroupScreenViewHolder(private val binding: GroupscreenMygroupsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.N)
    fun bind(postData: Group) {

        val bundle = bundleOf(
            "name" to postData.name,
            "id" to postData.id,
            "creatorId" to postData.creator?.id,
            "imageUrl" to postData.image?.url
        )

        binding.root.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_group_view_to_groupdetails, bundle)
        }

        Glide.with(binding.root)
            .load(postData.image?.url)
            .placeholder(R.drawable.hhhhhelp)
            .into(binding.groupImage)

        binding.tvGroupName.text = postData.name.toString()
        binding.tvCreatorName.text = postData.creator?.lastname.toString()
        binding.tvGroupInviter.text = postData.inviter?.lastname

    }

}

