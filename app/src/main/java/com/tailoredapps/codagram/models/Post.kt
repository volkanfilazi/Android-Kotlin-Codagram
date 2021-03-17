package com.tailoredapps.codagram.models

import java.io.Serializable
import java.sql.Time
import java.sql.Timestamp
import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.util.*

data class Post(
    val id:String,
    val user:User?,
    val group:Group?,
    val description:String?,
    val member:List<User>?,
    val image: Image?,
    val likes:Int?,
    var userLiked:Boolean = false,
    val comments:List<Comment>?,
    val createdAt: String?,
    val tags:List<User>
):Serializable

data class PostBody(
    val description:String,
    val group:String,
    val tags:List<String>,

)

data class Comment(
    val id:String,
    val user: User?,
    val text:String,
    val createdAt:String
):Serializable

data class CommentBody(
    val text: String
)


