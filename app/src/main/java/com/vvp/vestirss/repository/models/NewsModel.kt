package com.vvp.vestirss.repository.models

import androidx.room.Entity
import androidx.room.PrimaryKey

    // класс-модель для новости

@Entity
data class NewsModel(

    @PrimaryKey
    var title: String = "",

    var pubDate: String? = null,

    var category: String? = null,

    var imageUrl: String? = null,

    var fullText: String? = null
)





