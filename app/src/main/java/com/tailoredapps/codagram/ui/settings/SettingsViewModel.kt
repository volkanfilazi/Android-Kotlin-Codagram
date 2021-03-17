package com.tailoredapps.codagram.ui.settings

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.tailoredapps.Event
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.models.SendUser
import com.tailoredapps.codagram.models.User
import com.tailoredapps.codagram.remote.CodaGramApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.lang.Exception

class SettingsViewModel(private val context: Context, private val codaGramApi: CodaGramApi) :
    ViewModel() {

    val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    @ExperimentalCoroutinesApi
    private val myUser = MutableLiveData<User>()

    @ExperimentalCoroutinesApi
    fun getMyGroupMembers(): LiveData<User> = myUser

    @ExperimentalCoroutinesApi
    private fun updateMembersList(update: User) {
        viewModelScope.launch(Dispatchers.Main) {
            myUser.value = update
        }
    }
    fun getUsers() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = codaGramApi.getUser()
                updateMembersList(response)
            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }

    @ExperimentalCoroutinesApi
    fun updateNickName(nickname: String, firstName: String, lastName: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = codaGramApi.updateProfile(SendUser(nickname, firstName, lastName))

                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        statusMessage.value = Event(context.getString(R.string.statusUserUpdate))
                        getUsers()
                    } else {
                        statusMessage.value = Event(context.getString(R.string.statusError))
                    }
                }
            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }

    fun addPhoto(uri: Uri) {
        try {
            val file = File(uri.path!!)
            val requestBody: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val part: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", file.name, requestBody)
            viewModelScope.launch(Dispatchers.IO) {
                val response = codaGramApi.updateUserImage(part)

                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        statusMessage.value = Event(context.getString(R.string.updateImage))
                        getUsers()
                    } else {
                        statusMessage.value = Event(context.getString(R.string.statusError))

                    }
                }
            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }

    fun deleteUserImage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = codaGramApi.deleteUserImage()

                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        statusMessage.value = Event(context.getString(R.string.statusDeletePhoto))
                        getUsers()
                    } else {
                        statusMessage.value = Event(context.getString(R.string.statusError))
                    }
                }
            } catch (ie: Exception) {
                Timber.e(ie)
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun deleteUser() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = codaGramApi.deleteUser()

                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        statusMessage.value = Event(context.getString(R.string.statusUserDelete))
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