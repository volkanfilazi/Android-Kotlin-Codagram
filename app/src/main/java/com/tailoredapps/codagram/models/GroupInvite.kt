package com.tailoredapps.codagram.models

import com.tailoredapps.codagram.remoteModels.ReplyToInvite
import java.util.*
data class GroupInvite(
    val id: String,
    val inviter: User,
    var name: String,
    val group: Group,
    val invitedAt: String,
    val members:List<User>,
    var replyToInvite: Boolean = false

)

data class GroupInviteBody(
    val groupId:String?,
    val invitees:List<String>
)
