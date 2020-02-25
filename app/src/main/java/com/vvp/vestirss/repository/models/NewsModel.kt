package com.vvp.vestirss.repository.models

import androidx.room.Entity
import androidx.room.PrimaryKey

    // класс-модель для новости
    // тип Parcelable - для возможности передачи всего объекта
    // к экрану деталировки без разбора на поля


@Entity
data class NewsModel(

    var title: String,

    var pubDate: String,

    var category: String,

    var imageUrl: String,

    var fullText: String,

    @PrimaryKey(autoGenerate = true)
    var id: Int
)






