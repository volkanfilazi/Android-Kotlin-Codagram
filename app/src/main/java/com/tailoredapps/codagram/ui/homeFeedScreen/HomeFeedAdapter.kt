package com.tailoredapps.codagram.ui.homeFeedScreen

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.firebase.auth.FirebaseAuth
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.HomeFeedScreenBinding
import com.tailoredapps.codagram.models.Post
import com.tailoredapps.codagram.remote.CodaGramApi
import com.tailoredapps.codagram.remote.SessionManager
import kotlinx.android.synthetic.main.comment_screen_items.view.*
import kotlinx.android.synthetic.main.fragment_group.view.*
import kotlinx.android.synthetic.main.fragment_group_details.view.*
import kotlinx.android.synthetic.main.home_feed_screen.view.*
import kotlinx.android.synthetic.main.home_feed_screen.view.ivUserImage
import java.text.SimpleDateFormat
import java.util.*

class HomeFeedAdapter(val codaGramApi: CodaGramApi, val context: Context) :
    ListAdapter<Post, HomeFeedAdapter.CountryItem>(DiffCallback()) {
    lateinit var mItemCLicked: ItemCLickedListener
    lateinit var mItemRemoveClicked: ItemGroupRemoveListener
    lateinit var post: Post

    class DiffCallback : DiffUtil.ItemCallback<Post>() {

        override fun areContentsTheSame(
            oldItem: Post, newItem: Post
        ): Boolean {
            return oldItem.comments?.size == newItem.comments?.size && oldItem.likes == newItem.likes
        }

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: CountryItem, position: Int) {
        holder.bind(getItem(position))
        val currentItem = getItem(position)

        holder.apply {
            Glide.with(itemView)
                .load(currentItem.image?.url)
                .placeholder(R.drawable.person)
                .into(itemView.post_image)

            Glide.with(itemView)
                .load(currentItem.comments?.getOrNull(0)?.user?.image?.url)
                .placeholder(R.drawable.person)
                .into(itemView.ivUserImage)

            Glide.with(itemView)
                .load(currentItem.comments?.getOrNull(1)?.user?.image?.url)
                .placeholder(R.drawable.person)
                .into(itemView.ivUser2Image)

            Glide.with(itemView)
                .load(currentItem.user?.image?.url)
                .placeholder(R.drawable.person)
                .into(itemView.user_photo_image)


            myBinding.usernameText.text = currentItem.user?.firstname
            myBinding.likesText.text = currentItem.likes.toString()
            myBinding.tvTagSize.text = currentItem.tags.size.toString()
            myBinding.tvGroupName.text = currentItem.group?.name.toString()
            myBinding.tvNickName.text = "(" + currentItem.user?.nickname.toString() + ")"
            myBinding.tvTime.text = "${currentItem.createdAt?.let { convertDateString(it) }}"

            if (currentItem.comments == null || currentItem.comments.isEmpty()) {
                myBinding.cl1.isVisible = false
            } else if (currentItem.comments?.size!! > 1) {
                myBinding.lastCvv.visibility = View.VISIBLE
                myBinding.tvWrittenBy.text =
                    currentItem.comments?.get(0)?.user?.firstname.toString()
                myBinding.tvFirstComment.text = currentItem.comments?.get(0)?.text

                myBinding.tvWrittenBy2.text =
                    currentItem.comments?.get(1)?.user?.firstname.toString()
                myBinding.tvFirstComment2.text = currentItem.comments?.get(1)?.text.toString()
            } else if (currentItem.comments?.size == 1) {
                myBinding.tvWrittenBy.text =
                    currentItem.comments?.get(0)?.user?.firstname.toString()
                myBinding.tvFirstComment.text = currentItem.comments?.get(0)?.text.toString()
                myBinding.cl2.isVisible = false

            }

            myBinding.commentText.text = currentItem.comments?.size.toString() + " " + "Comment"

            if (currentItem.userLiked) {
                myBinding.likeImage.setImageResource(R.drawable.ic_baseline_favoritelike_true_24)
            } else {
                myBinding.likeImage.setImageResource(R.drawable.ic_baseline_favorite_border_false_24)
            }


            myBinding.likeImage.setOnClickListener {
                myBinding.likeImage.setImageResource(R.drawable.ic_baseline_favoritelike_true_24)
                currentItem.userLiked = currentItem.userLiked.not()

                when {
                    currentItem.userLiked -> {
                        mItemCLicked.let {
                            mItemCLicked.onItemClicked(true, getItem(position))
                            myBinding.likeImage.setImageResource(R.drawable.ic_baseline_favoritelike_true_24)
                            if (FirebaseAuth.getInstance().currentUser!!.uid == currentItem.user?.id) {
                                myBinding.likeImage.setImageResource(R.drawable.ic_baseline_favoritelike_true_24)
                            }
                        }
                    }
                    else -> {
                        mItemCLicked.onItemClicked(false, getItem(position))

                        myBinding.likeImage.setImageResource(R.drawable.ic_baseline_favorite_border_false_24)
                    }
                }
            }

            when (FirebaseAuth.getInstance().currentUser!!.uid) {
                currentItem.user?.id -> myBinding.ivDelete.visibility = View.VISIBLE
                else -> myBinding.ivDelete.visibility = View.INVISIBLE
            }

            when {
                FirebaseAuth.getInstance().currentUser!!.uid == currentItem.user?.id -> myBinding.ivDelete.visibility = View.VISIBLE
                else -> myBinding.ivDelete.visibility = View.INVISIBLE
            }

            myBinding.ivDelete.setOnClickListener {
                mItemRemoveClicked.let {
                    mItemRemoveClicked.onGroupRemoved(getItem(position))
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryItem {
        return CountryItem(
            HomeFeedScreenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun setUpListener(itemCLicked: ItemCLickedListener) {
        mItemCLicked = itemCLicked
    }

    fun removeUpListener(itemRemoved: ItemGroupRemoveListener) {
        mItemRemoveClicked = itemRemoved
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun convertDateString(oldString: String): String {

        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
        val targetFormat = SimpleDateFormat("MM-dd-yyyy")
        val date: Date = originalFormat.parse(oldString)

        return targetFormat.format(date)

    }

    class CountryItem(val binding: HomeFeedScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val myBinding = binding

        fun bind(postData: Post) {

            val bundle = bundleOf(
                "name" to postData.id,
                "image" to postData.user?.image?.url
            )

            binding.captionText.text = postData.description.toString()
            binding.commentImage.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_firstView_to_CommentScreenFragment, bundle)
            }


            binding.commentText.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_firstView_to_CommentScreenFragment, bundle)
            }

            binding.lastCvv.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_firstView_to_CommentScreenFragment, bundle)
            }

        }

    }

    interface ItemCLickedListener {
        fun onItemClicked(like: Boolean, post: Post)
    }

    interface ItemGroupRemoveListener {
        fun onGroupRemoved(post: Post)
    }
}
