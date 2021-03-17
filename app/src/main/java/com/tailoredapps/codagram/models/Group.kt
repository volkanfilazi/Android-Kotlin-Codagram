package com.tailoredapps.codagram.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Group(
    val creator: User?,
    val id: String,
    val image: Image?,
    val members: List<User>,
    val name: String,
    val inviter:User,

):Serializable


data class GroupCreate(
   val name: String,
   val members: List<String>,
)



