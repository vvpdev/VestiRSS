package com.vvp.vestirss.repository.models

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

    // класс-модель для новости
    // тип Parcelable - для возможности передачи всего объекта
    // к экрану деталировки без разбора на поля


@Entity
data class NewsModel(


    @PrimaryKey (autoGenerate = true)
    @NonNull
    var id: Int? = null,

    var title: String? = null,

    var pubDate: String? = "",

    var category: String? = null,

    var imageUrl: String? = null,

    var fullText: String? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(title)
        parcel.writeString(pubDate)
        parcel.writeString(category)
        parcel.writeString(imageUrl)
        parcel.writeString(fullText)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewsModel> {
        override fun createFromParcel(parcel: Parcel): NewsModel {
            return NewsModel(parcel)
        }

        override fun newArray(size: Int): Array<NewsModel?> {
            return arrayOfNulls(size)
        }
    }
}