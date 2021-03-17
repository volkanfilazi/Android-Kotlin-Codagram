package com.tailoredapps.codagram.ui.homeFeedScreen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailoredapps.Event
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.models.Comment
import com.tailoredapps.codagram.models.CommentBody
import com.tailoredapps.codagram.models.Post
import com.tailoredapps.codagram.remote.CodaGramApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class CommentScreenViewModel(private val context: Context, private val codaGramApi: CodaGramApi) :
    ViewModel() {

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    @ExperimentalCoroutinesApi
    private val myPost = MutableLiveData<Post>()

    @ExperimentalCoroutinesApi
    fun getMyPost(): LiveData<Post> = myPost

    @ExperimentalCoroutinesApi
    private val myCommentsObject = MutableLiveData<Comment>()

    @ExperimentalCoroutinesApi
    fun getMyCommentsObject(): LiveData<Comment> = myCommentsObject

    @ExperimentalCoroutinesApi
    private val myComments = MutableLiveData<List<Comment>>()

    @ExperimentalCoroutinesApi
    fun getMyComments(): LiveData<List<Comment>> = myComments


    fun getMyCommentsObjecSt(id: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                codaGramApi.getComment(id)
            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }

    fun updateCommentbyObject(comment: Comment) {
        viewModelScope.launch(Dispatchers.Main) {
            myCommentsObject.value = comment
        }
    }

    fun getPostById(id: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                codaGramApi.getPostId(id)
            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }

    fun getCommentPost(id: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = codaGramApi.getComment(id)
                updateComment(response.comments)
            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }

    @ExperimentalCoroutinesApi
    fun updateComment(comment: List<Comment>) {
        viewModelScope.launch(Dispatchers.Main) {
            myComments.value = comment
        }
    }

    @ExperimentalCoroutinesApi
    fun deleteComment(id: String, commentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = codaGramApi.deleteComment(id, commentId)

            viewModelScope.launch(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val update = codaGramApi.getComment(id)
                    updateComment(update.comments)
                    statusMessage.value = Event(context.getString(R.string.statusDeleteComment))
                } else {
                    statusMessage.value = Event(context.getString(R.string.statusError))
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun postComment(id: String, text: CommentBody) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = codaGramApi.postComment(id, text)
            if (response.isSuccessful) {
                val updates = codaGramApi.getComment(id)
                updateComment(updates.comments)
            }
        }
    }


}