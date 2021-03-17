package com.tailoredapps.codagram

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.tailoredapps.codagram.remote.remoteModule
import com.tailoredapps.codagram.ui.groupscreen.groupScreenModule
import com.tailoredapps.codagram.ui.homeFeedScreen.firstViewModule
import com.tailoredapps.codagram.ui.loginscreen.loginScreenModule
import com.tailoredapps.codagram.ui.newStoryScreen.newStoryScreenModule
import com.tailoredapps.codagram.ui.settings.SettingsModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.immutableListOf
import org.koin.dsl.module
import java.io.IOException

internal val appModule = module { single { provideGlobalAppData() } }

private fun provideGlobalAppData(): GlobalAppData = GlobalAppData(
    baseUrl = "https://codagram.tailored-apps.com/api/"
)

data class GlobalAppData(
    val baseUrl: String = ""
)

private const val  X_FIREBASE_TOKEN: String = "X-FIREBASE-TOKEN"
class FirebaseUserIdTokenInterceptor() :Interceptor{
private val     baseUrl = "https://codagram.tailored-apps.com/api/"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                throw  Exception("User is not logged in.");
            } else {
                val task: Task<GetTokenResult> = user.getIdToken(true)
                val tokenResult: GetTokenResult = Tasks.await(task)
                val idToken: String? = tokenResult.token
                if (idToken == null) {
                    throw  Exception("idToken is null");
                } else {
                    val modifiedRequest: Request = request.newBuilder()
                        .addHeader(X_FIREBASE_TOKEN, idToken)
                        .build()

                    val modifiedRequests: Request.Builder = request.newBuilder()
                    val glideUrl = GlideUrl(baseUrl,
                        LazyHeaders.Builder()
                            .addHeader("X-FIREBASE-TOKEN",idToken)
                            .build())
                    return chain.proceed(modifiedRequest)
                }
            }
        }catch (e: java.lang.Exception){
            throw IOException(e.message)
        }
    }


}



@ExperimentalCoroutinesApi
internal val appModules = immutableListOf(
    appModule,
    firstViewModule,
    remoteModule,
    loginScreenModule,
    SettingsModule,
    groupScreenModule,
    newStoryScreenModule
)
