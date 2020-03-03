package com.vvp.vestirss.repository.storage.models

import androidx.room.Entity
import androidx.room.PrimaryKey

    // класс-модель для новости

@Entity
data class NewsModel(

    @PrimaryKey (autoGenerate = true)
    var id: Int = 0,

    var title: String? = null,

    var pubDate: String? = null,

    var category: String? = null,

    var imageUrl: String? = null,

    var fullText: String? = null
)





