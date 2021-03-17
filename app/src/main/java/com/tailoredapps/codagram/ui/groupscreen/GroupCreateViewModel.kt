package com.tailoredapps.codagram.ui.groupscreen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailoredapps.Event
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.models.GroupCreate
import com.tailoredapps.codagram.models.GroupInviteBody
import com.tailoredapps.codagram.remote.CodaGramApi
import com.tailoredapps.codagram.remoteModels.SelectedUser
import com.tailoredapps.codagram.ui.newStoryScreen.NewStoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File


class GroupViewModel(private val context: Context, private val codaGramApi: CodaGramApi) :
    ViewModel() {

    open class Event{
        data class ShowMessage(val msg:String) : Event()
        object Navigate : Event()
    }

    private val events = MutableLiveData<NewStoryViewModel.Event>()

    fun getEvents(): LiveData<NewStoryViewModel.Event> = events


    private val searchForUser = MutableLiveData<List<SelectedUser>>()

    @ExperimentalCoroutinesApi
    fun getSearchedUser(): LiveData<List<SelectedUser>> = searchForUser


    @ExperimentalCoroutinesApi
    fun searchUser(input: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = codaGramApi.getSearchedUser(input)
                updateSearchList(response.users.map { (SelectedUser(it)) })
            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }

    @ExperimentalCoroutinesApi
    fun updateSearchList(userSearch: List<SelectedUser>) {
        viewModelScope.launch(Dispatchers.Main) {
            searchForUser.value = userSearch
        }

    }

    @ExperimentalCoroutinesApi
    fun createGroup(group: String, uri: Uri) {
        Timber.e("${searchForUser.value}")
        val file = File(uri.path!!)
        val requestBody: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val part: MultipartBody.Part =
            MultipartBody.Part.createFormData("image", file.name, requestBody)
        try {

            viewModelScope.launch(Dispatchers.IO) {
                val selectedUsers = searchForUser.value?.filter { it.selected }?.map { it.user.id }
                val response =
                    codaGramApi.createGroup(GroupCreate(group, selectedUsers as List<String>))
                        .also {
                            it.body()?.let { it1 -> codaGramApi.addImageToGroup(it1.id, part) }
                            it.body()?.id?.let { it1 -> codaGramApi.getGroupbyId(it1) }
                            codaGramApi.sendGroupInvites(
                                GroupInviteBody(
                                    it.body()?.id,
                                    selectedUsers as List<String>,
                                )
                            )

                        }

                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful){
                        events.value = NewStoryViewModel.Event.Navigate
                        events.value = NewStoryViewModel.Event.ShowMessage(context.getString(R.string.eventGroupCreated))
                    } else{
                        events.value = NewStoryViewModel.Event.ShowMessage(context.getString(R.string.statusError))
                    }
                }


            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }

    }


}