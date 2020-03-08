package com.vvp.vestirss.repository.storage.tools

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class NewsQuantity (

    @PrimaryKey
    var id: Int,

    var quantity: Int
)