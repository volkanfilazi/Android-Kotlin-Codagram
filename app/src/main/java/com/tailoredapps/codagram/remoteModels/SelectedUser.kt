package com.tailoredapps.codagram.remoteModels

import com.tailoredapps.codagram.models.User

data class SelectedUser(
    val user: User,
    var selected: Boolean = false
)


