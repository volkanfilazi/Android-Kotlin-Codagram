package com

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.module.AppGlideModule
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.play.core.appupdate.AppUpdateOptions.newBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.tailoredapps.codagram.FirebaseUserIdTokenInterceptor
import com.tailoredapps.codagram.remote.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.addHeaderLenient
import java.io.IOException
import java.io.InputStream
import java.nio.file.attribute.AclEntry.newBuilder

private const val X_FIREBASE_TOKEN: String = "X-FIREBASE-TOKEN"

@GlideModule
class AppGlideModule(val context: Context) : AppGlideModule() {
    private val sessionManager = SessionManager(context)
    private lateinit var token: String
    private lateinit var url: String

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        val client = OkHttpClient.Builder().addInterceptor(FirebaseUserIdTokenInterceptor()).build()
        glide.registry.replace(GlideUrl::class.java,InputStream::class.java,OkHttpUrlLoader.Factory(client))

        /*    super.registerComponents(context, glide, registry)

        token = sessionManager.fetchAuthToken().toString()
        url = "https://codagram.tailored-apps.com/api/"

        val glideUrl = GlideUrl(
            url,
            LazyHeaders.Builder()
                .addHeader("X-FIREBASE-TOKEN", token)
                .build()
        )


    }

    private val baseUrl = "https://codagram.tailored-apps.com/api/"

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
                    val glideUrl = GlideUrl(
                        baseUrl,
                        LazyHeaders.Builder()
                            .addHeader("X-FIREBASE-TOKEN", idToken)
                            .build()
                    )
                    return chain.proceed(modifiedRequest)
                }
            }
        } catch (e: java.lang.Exception) {
            throw IOException(e.message)
        }
    }*/

    }
}
/* val url = "https://codagram.tailored-apps.com/api/"
 val glideUrl = GlideUrl(url,
  LazyHeaders.Builder()
   .addHeader("X-FIREBASE-TOKEN", SessionManager.USER_TOKEN)
   .build()
 )
 override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
*//*  val url = "https://codagram.tailored-apps.com/api/"
  val glideUrl = GlideUrl(url,
   LazyHeaders.Builder()
    .addHeader("X-FIREBASE-TOKEN", SessionManager.USER_TOKEN)
    .build()*//*

  super.registerComponents(context, glide, registry)
  val httpClient = OkHttpClient.Builder()
  httpClient.addInterceptor { chain: Interceptor.Chain? ->
   val user = FirebaseAuth.getInstance().currentUser
   val response = chain?.proceed(chain.request())
   val task: Task<GetTokenResult> = user!!.getIdToken(true)
   val tokenResult: GetTokenResult = Tasks.await(task)

   val idToken: String? = tokenResult.token
   // for get specific header
   val header = response?.header("X-FIREBASE-TOKEN")
   // for get all headers
   val headers = response?.headers

   response!!

  }

  registry.replace(GlideUrl::class.java, InputStream::class.java,
   OkHttpUrlLoader.Factory(httpClient.build()))
 }


 override fun applyOptions(context: Context, builder: GlideBuilder) {
  super.applyOptions(context, builder)
 }

 override fun isManifestParsingEnabled(): Boolean {
  return super.isManifestParsingEnabled()
 }
 class FirebaseUserIdTokenInterceptor() : Interceptor {
  private  val  X_FIREBASE_TOKEN: String = "X-FIREBASE-TOKEN"

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
      return chain.proceed(modifiedRequest)
     }
    }
   }catch (e: java.lang.Exception){
    throw IOException(e.message)
   }
  }

 }*/


