package com.tailoredapps.codagram.ui.homeFeedScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.CommentScreenItemsBinding
import com.tailoredapps.codagram.databinding.FragmentCommentScreenBinding
import com.tailoredapps.codagram.models.Comment
import com.tailoredapps.codagram.models.CommentBody
import com.tailoredapps.codagram.models.KEY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject


class CommentScreenFragment : Fragment() {

    private val viewModel: CommentScreenViewModel by inject()
    private val commentScreenAdapter: CommentScreenAdapter by inject()
    lateinit var commentAdapterBinding: CommentScreenItemsBinding

    var postIds: String? = null
    var postImage: String? = null
    var image: String? = null

    private lateinit var binding: FragmentCommentScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postIds = arguments?.getString(KEY.postIds).toString()
        postImage = arguments?.getString(KEY.postImage).toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCommentScreenBinding.inflate(inflater, container, false)
        commentAdapterBinding = CommentScreenItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCommentsRecyclerview.apply {
            adapter = this@CommentScreenFragment.commentScreenAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        bindToLiveData()
        getTest()
        viewModel.getCommentPost(postIds.toString())
        postComment()
        bindComments()
        myMessage()

        viewModel.getMyCommentsObjecSt(postIds.toString())
    }

    fun getTest() {
        viewModel.getPostById(postIds.toString())
    }

    @ExperimentalCoroutinesApi
    fun bindToLiveData() {
        viewModel.getMyComments().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            commentScreenAdapter.submitList(it)
            commentScreenAdapter.notifyDataSetChanged()
        })
        commentScreenAdapter.setUpListener(object : CommentScreenAdapter.ItemRemove2ClickListener {
            override fun onItemClicked(comment: Comment) {
                viewModel.deleteComment(postIds.toString(), comment.id)
            }
        })
    }

    @ExperimentalCoroutinesApi
    fun bindComments() {
        viewModel.getMyCommentsObject().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

        })
    }


    @ExperimentalCoroutinesApi
    fun postComment() {
        binding.btnAdd.setOnClickListener {
            val test = binding.etWriteComment.text.toString()
            if (test.isNotEmpty()) {
                viewModel.postComment(postIds.toString(), CommentBody(test))
                viewModel.getPostById(postIds.toString())
                binding.etWriteComment.setText("")

            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.snackMinCharakte),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    fun myMessage() {
        viewModel.message.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
            }
        })
    }
}