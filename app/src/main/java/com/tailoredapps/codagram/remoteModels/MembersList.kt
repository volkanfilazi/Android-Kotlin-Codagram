package com.tailoredapps.codagram.remoteModels

import com.tailoredapps.codagram.models.Group
import com.tailoredapps.codagram.models.GroupInvite
import com.tailoredapps.codagram.models.User

data class InvitesList(
    val invites:List<GroupInvite>,
)