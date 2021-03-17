package com.tailoredapps.codagram.ui.groupscreen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailoredapps.Event
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.models.Group
import com.tailoredapps.codagram.models.GroupInvite
import com.tailoredapps.codagram.remote.CodaGramApi
import com.tailoredapps.codagram.remoteModels.ReplyToInvite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber

@ExperimentalCoroutinesApi
class MyGroupScreenViewMode(private val context: Context, private val codaGramApi: CodaGramApi) :
    ViewModel() {

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    @ExperimentalCoroutinesApi
    private val searchForUser = MutableLiveData<GroupInvite>()


    @ExperimentalCoroutinesApi
    fun getSearchedUser(): LiveData<GroupInvite> = searchForUser

    @ExperimentalCoroutinesApi
    private val myInvites = MutableLiveData<List<GroupInvite>>()

    @ExperimentalCoroutinesApi
    fun getMyInvites(): LiveData<List<GroupInvite>> = myInvites

    @ExperimentalCoroutinesApi
    private val myGroups = MutableLiveData<List<Group>>()

    @ExperimentalCoroutinesApi
    fun getMyGroups(): LiveData<List<Group>> = myGroups


    fun getAllGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = codaGramApi.getAllGroups()
            response.body()?.let { updateUi(it.groups) }

            viewModelScope.launch(Dispatchers.Main) {
                if (response.body()?.groups?.isEmpty() == true)
                    statusMessage.value = Event(context.getString(R.string.eventNoGroups))
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun updateUi(update: List<Group>) {
        viewModelScope.launch(Dispatchers.Main) {
            myGroups.value = update
        }
    }

    fun getInvites() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = codaGramApi.getGroupInvitees()
                response.body()?.invites?.let { updateList(it) }
                searchForUser.value?.replyToInvite

                viewModelScope.launch(Dispatchers.Main) {
                    if (!response.isSuccessful) {
                        statusMessage.value = Event(context.getString(R.string.statusError))
                    }
                }
            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }

    @ExperimentalCoroutinesApi
    private fun updateList(update: List<GroupInvite>) {
        viewModelScope.launch(Dispatchers.Main) {
            myInvites.value = update
        }
    }

    fun answerInvites(id: String, accept: Boolean) {
        var response: Response<Unit>
        try {
            viewModelScope.launch(Dispatchers.IO) {
                response = codaGramApi.replyToanyInvite(id, ReplyToInvite(accept))

                viewModelScope.launch(Dispatchers.Main) {
                    searchForUser.value?.replyToInvite
                    if (response.isSuccessful) {
                        statusMessage.value = Event(context.getString(R.string.statusGroupInvite))

                        getAllGroups()
                        getInvites()

                    } else {
                        statusMessage.value = Event(context.getString(R.string.statusError))

                    }
                }

            }

        } catch (ie: Exception) {
            Timber.e(ie)

        }
    }
}