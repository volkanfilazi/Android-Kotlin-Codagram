package com.tailoredapps.codagram.ui.homeFeedScreen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailoredapps.Event
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.models.Group
import com.tailoredapps.codagram.models.Post
import com.tailoredapps.codagram.remote.CodaGramApi
import com.tailoredapps.codagram.remoteModels.CommentLike
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeFeedViewModel(private val context: Context, val codaGramApi: CodaGramApi) : ViewModel() {
    val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    @ExperimentalCoroutinesApi
    private val myPosts = MutableLiveData<List<Post>>()

    @ExperimentalCoroutinesApi
    fun getMyPost(): LiveData<List<Post>> = myPosts

    @ExperimentalCoroutinesApi
    private val myGroups = MutableLiveData<List<Group>>()

    @ExperimentalCoroutinesApi
    fun getMyGroups(): LiveData<List<Group>> = myGroups


    @ExperimentalCoroutinesApi
    fun getStoryPost(id: String?) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = codaGramApi.getStoryPost(id)
                response.body()?.let { updateHomeFeed(it.posts) }

                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        statusMessage.value = Event(context.getString(R.string.statusListLoad))
                    } else if (response.body()?.posts?.isEmpty() == true) {
                        statusMessage.value = Event(context.getString(R.string.statusNoPosts))
                    } else {
                        statusMessage.value = Event(context.getString(R.string.statusError))

                    }
                }

            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }


    @ExperimentalCoroutinesApi
    fun getStoryPostByQuery(id: String?) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = codaGramApi.getStoryPostbyQuery(id)
                response.body()?.posts?.let { updateHomeFeed(it) }

                viewModelScope.launch(Dispatchers.Main) {

                    if (response.isSuccessful) {
                        statusMessage.value = Event(context.getString(R.string.statusGroup))
                    } else if (response.body()?.posts?.isEmpty() == true) {
                        statusMessage.value = Event(context.getString(R.string.statusNoPosts))
                    } else {
                        statusMessage.value = Event(context.getString(R.string.statusError))

                    }
                }


            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }

    fun getAllGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = codaGramApi.getAllGroups()
            response.body()?.groups?.let { updateUi(it) }
        }
    }

    @ExperimentalCoroutinesApi
    private fun updateUi(update: List<Group>) {
        viewModelScope.launch(Dispatchers.Main) {
            myGroups.value = update
        }
    }


    @ExperimentalCoroutinesApi
    fun updateHomeFeed(update: List<Post>) {
        viewModelScope.launch(Dispatchers.Main) {
            myPosts.value = update
        }

    }

    @ExperimentalCoroutinesApi
    fun likeComment(id: String, like: Boolean) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = codaGramApi.likeToComment(id, CommentLike(like))

                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        statusMessage.value = Event(context.getString(R.string.likePost))

                        getStoryPost(id)
                    } else {
                        statusMessage.value = Event(context.getString(R.string.statusError))

                    }
                }

            }

        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }

    @ExperimentalCoroutinesApi
    fun removePost(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = codaGramApi.deletePost(id)

                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        statusMessage.value = Event(context.getString(R.string.statusRemovePost))
                        getStoryPost(id)
                    } else {
                        statusMessage.value = Event(context.getString(R.string.statusError))

                    }

                }


            } catch (ie: Exception) {

            }


        }
    }

}