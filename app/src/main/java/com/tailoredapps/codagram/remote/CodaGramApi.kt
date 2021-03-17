package com.tailoredapps.codagram.remote

import com.tailoredapps.codagram.models.*
import com.tailoredapps.codagram.remoteModels.*
import retrofit2.Response
import com.tailoredapps.codagram.remoteModels.PostList
import okhttp3.MultipartBody
import retrofit2.http.*

interface CodaGramApi {
    @POST("user")
    suspend fun addUser(@Body user: SendUser): User

    @GET("user")
    suspend fun getUser(): User

    @GET("user/search")
    suspend fun getSearchedUser(@Query("query") input: String?): SearchResult

    @PUT("user")
    suspend fun updateProfile(@Body user: SendUser): Response<User>

    @DELETE("user")
    suspend fun deleteUser(): Response<Unit>

    @DELETE("user/image")
    suspend fun deleteUserImage(): Response<Unit>

    @Multipart
    @PUT("user/image")
    suspend fun updateUserImage(@Part file: MultipartBody.Part): Response<Unit>
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                   GROUP ///

    @POST("group")
    suspend fun createGroup(@Body group: GroupCreate): Response<Group>

    @GET("group")
    suspend fun getAllGroups(): Response<GroupList>

    @POST("group/invites")
    suspend fun sendGroupInvites(@Body groupInviteBody: GroupInviteBody?): Response<Unit>

    @GET("group/{id}")
    suspend fun getGroupbyId(@Path("id") id: String): Group

    @GET("group/invites")
    suspend fun getGroupInvitees(): Response<InvitesList>

    @PUT("group/invites/{id}")
    suspend fun replyToanyInvite(@Path("id") id: String, @Body accept: ReplyToInvite?
    ): Response<Unit>

    @POST("post/{id}/like")
    suspend fun likeToComment(@Path("id") id: String, @Body like: CommentLike?): Response<Unit>

    @DELETE("group/{id}")
    suspend fun deleteGroup(@Path("id") id: String): Response<Unit>

    @DELETE("group/{id}/remove/{uId}")
    suspend fun deleteMember(@Path("id") id: String, @Path("uId") uId: String): Response<Unit>

    @DELETE("group/{id}/exit")
    suspend fun exitGroup(@Path("id") id: String): Response<Unit>

    @DELETE("group/{id}/image")
    suspend fun deleteGroupImage(@Path("id") id: String): Response<Unit>

    @Multipart
    @PUT("group/{id}/image")
    suspend fun addImageToGroup(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Response<Unit>


    @PUT("group/{id}")
    suspend fun updateGroup(@Path("id") id: String, @Body updateGroup: UpdateGroup): Response<Group>


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                   POST ///
    @POST("post")
    suspend fun newStoryPost(@Body postBody: PostBody): Response<Post>

    @GET("post")
    suspend fun getStoryPostbyQuery(@Query("group") id: String?): Response<PostList>


    @GET("post")
    suspend fun getStoryPost(@Query("grou") id: String?): Response<PostList>

    @GET("post/{id}")
    suspend fun getPostId(@Path("id") id: String): Post

    @POST("post/{id}/comment")
    suspend fun postComment(
        @Path("id") id: String,
        @Body commentBody: CommentBody
    ): Response<CommentList>

    @GET("post/{id}/comment")
    suspend fun getComment(@Query("id") id: String?): CommentList

    @Multipart
    @POST("post/{id}/image")
    suspend fun addPhoto(@Path("id") id: String, @Part file: MultipartBody.Part): Response<Unit>

    @DELETE("post/{id}")
    suspend fun deletePost(@Path("id") id: String): Response<Unit>

    @DELETE("post/{id}/comment/{commentId}")
    suspend fun deleteComment(
        @Path("id") id: String,
        @Path("commentId") commentId: String
    ): Response<Unit>


}