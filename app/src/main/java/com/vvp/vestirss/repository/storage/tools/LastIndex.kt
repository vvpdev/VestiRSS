package com.vvp.vestirss.repository.storage.tools

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class LastIndex (

    @PrimaryKey
    var id: Int,

    var lastIndex: Int
)