package com.vvp.vestirss.repository.storage.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class LastIndex (

    @PrimaryKey
    var id: Int,

    var lastIndex: Int
)