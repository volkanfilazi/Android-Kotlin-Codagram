package com.tailoredapps.codagram.ui.loginscreen

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.models.SendUser
import com.tailoredapps.codagram.remote.CodaGramApi
import com.tailoredapps.codagram.remote.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File


class LoginViewModel(private val context: Context, private val codaGramApi: CodaGramApi) :
    ViewModel() {

    private lateinit var auth: FirebaseAuth
    private val sessionManager = SessionManager(context)


    fun getToken() {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    val idToken: String = task.result!!.token.toString()
                    Log.e("idtoken", idToken)
                    sessionManager.saveAuthToken(idToken)
                    val ok=  sessionManager.fetchAuthToken()
                    Log.e("savetoken", "dssd")

                } else {
                    // Handle error -> task.getException();
                }
            }
    }


    fun postUser(user: SendUser) {

        try {
            viewModelScope.launch(Dispatchers.IO) {

                codaGramApi.addUser(user)
            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }

    }

    fun getUser(){
        try {
            viewModelScope.launch(Dispatchers.IO) {
                codaGramApi.getUser()
            }
        }catch (ie:Exception){
            Timber.e(ie)
        }
    }



    fun infoMessage(nameStatusIcon: ImageView,lastNameStatusIcon: ImageView,nickNameStatusIcon: ImageView,eMailStatusIcon: ImageView,passwordStatusIcon:ImageView){
        nameStatusIcon.setOnClickListener {
            Toast.makeText(context, R.string.register_firstName_info, Toast.LENGTH_LONG).show()
        }
        lastNameStatusIcon.setOnClickListener {
            Toast.makeText(context,R.string.register_lastName_info, Toast.LENGTH_LONG).show()
        }
        nickNameStatusIcon.setOnClickListener {
            Toast.makeText(context,R.string.register_nickName_info, Toast.LENGTH_LONG).show()
        }
        eMailStatusIcon.setOnClickListener {
            Toast.makeText(context,R.string.register_email_info, Toast.LENGTH_LONG).show()
        }
        passwordStatusIcon.setOnClickListener {
            Toast.makeText(context,R.string.register_password_info, Toast.LENGTH_LONG).show()
        }
    }


    fun  statusRulesFirstName(firstName: EditText,lastNameStatusIcon: ImageView):Boolean{

        var fn:String = firstName.text.toString()

        if(fn.isEmpty()){
            firstName.error = context.getString(R.string.firstNameNotEmpty)
            lastNameStatusIcon.setImageResource(R.drawable.icons8_cancel_24px_2)
            return false
        }

        else if(fn.length < 3){
            firstName.error = context.getString(R.string.register_firstName_info)
            lastNameStatusIcon.setImageResource(R.drawable.icons8_cancel_24px_2)
            return false
        }

        else{
            lastNameStatusIcon.setImageResource(R.drawable.icons8_ok_24px)
            return true
        }
    }

    fun statusRulesLastName(lastName: EditText,lastNameStatusIcon: ImageView):Boolean{

        var ln = lastName.text.toString()
         if(ln.isEmpty()){
            lastName.error=(context.getString(R.string.lastNameNotEmpty))
            lastNameStatusIcon.setImageResource(R.drawable.icons8_cancel_24px_2)
             return false
        }

        else if(ln.length < 3){
             lastName.error = context.getString(R.string.register_lastName_info)
             lastNameStatusIcon.setImageResource(R.drawable.icons8_cancel_24px_2)
             return false
         }

        else{
            lastNameStatusIcon.setImageResource(R.drawable.icons8_ok_24px)
             return true
        }

    }


    fun statusRulesNickName(nickName: EditText,nickNameStatusIcon: ImageView):Boolean{
        val nn = nickName.text.toString()
        if(nn.isEmpty()){
            nickName.setError(context.getString(R.string.nickNameNotEmpty))
            nickNameStatusIcon.setImageResource(R.drawable.icons8_cancel_24px_2)
            return false
        }

        else if (nn.length < 4){
            nickName.error = (context.getString(R.string.register_nickName_info))
            nickNameStatusIcon.setImageResource(R.drawable.icons8_cancel_24px_2)
            return false
        }

        else{
            nickNameStatusIcon.setImageResource(R.drawable.icons8_ok_24px)
            return true
        }
    }

    fun statusRulesEmail(email: EditText):Boolean{
        var emailPattern:String = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val em:String = email.text.toString()

        if(em.isEmpty()){

            email.error = context.getString(R.string.emailNotEmpty)
            return false
        }
        else if(em.length < 5){
            email.error = context.getString(R.string.register_email_info)
            return false
        }

        else{
            return true
        }
    }

    fun statusRulesPassword(password: EditText,passwordStatusIcon: ImageView):Boolean{
        val pw = password.text.toString()
        if(pw.isEmpty()){
            password.error = context.getString(R.string.pwNotEmpty)
            passwordStatusIcon.setImageResource(R.drawable.icons8_cancel_24px_2)
            return false
        }

        else if (pw.length < 5){
            password.error = context.getString(R.string.register_password_info)
            passwordStatusIcon.setImageResource(R.drawable.icons8_cancel_24px_2)
            return false
        }

        else{
            passwordStatusIcon.setImageResource(R.drawable.icons8_ok_24px)
            return true
        }
    }

    fun addPhoto(uri: Uri){
        try {
            val file = File(uri.path!!)
            val requestBody: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestBody)
            viewModelScope.launch(Dispatchers.IO) {

                codaGramApi.updateUserImage(part)

            }
        } catch (ie: Exception) {
            Timber.e(ie)
        }
    }



}



