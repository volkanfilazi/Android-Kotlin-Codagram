package com.tailoredapps.codagram.models

data class User(
    val nickname: String,
    val firstname: String,
    val lastname: String,
    val id: String?,
    val image: Image?,
    val email: String?,

)
data class SendUser(
    val nickname: String,
    val firstname: String,
    val lastname: String

)

data class Image(
    val url: String?,
)


