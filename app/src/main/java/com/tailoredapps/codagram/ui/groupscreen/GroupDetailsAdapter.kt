package com.tailoredapps.codagram.ui.groupscreen


import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.GroupscreenMygroupsBinding
import com.tailoredapps.codagram.databinding.SearchDetailPageBinding
import com.tailoredapps.codagram.models.Group
import com.tailoredapps.codagram.models.User
import kotlinx.android.synthetic.main.search_detail_page.view.*

class GroupDetailsAdapter : ListAdapter<User, GroupDetailsAdapter.GroupDetailsSearchViewHolder>(
    DiffCallback()
) {
    lateinit var mItemCLicked: ItemRemoveClickListener


    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupDetailsSearchViewHolder {
        return GroupDetailsAdapter.GroupDetailsSearchViewHolder(
            SearchDetailPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun setUpListener(itemCLicked: ItemRemoveClickListener) {
        mItemCLicked = itemCLicked
    }


    class GroupDetailsSearchViewHolder(private val binding: SearchDetailPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name: TextView = itemView.findViewById(R.id.resultText)

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(postData: User) {

            binding.resultText.text = postData.nickname
            binding.tvUserNameMemberCards.text = postData.firstname
            binding.tvLastNameMemberCards.text = postData.lastname
            binding.tvEmailMemberCards.text = postData.email

            Glide.with(itemView)
                .load(postData.image?.url)
                .placeholder(R.drawable.person)
                .into(binding.ivUserProfileImage)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: GroupDetailsSearchViewHolder, position: Int) {
        holder.bind(getItem(position))
        val selectedItem = getItem(position)
        holder.itemView.setOnLongClickListener {
            holder.apply {
                mItemCLicked.let {
                    mItemCLicked.onItemClicked(getItem(position))
                }

            }
            true


        }
    }

    interface ItemRemoveClickListener {
        fun onItemClicked(user: User)
    }

}
