package com.vvp.vestirss.repository.storage.models

    //data class без дополнительных полей, которые не нужны для отображения в recyclerView

data class MinNewsModel (

    var id: Int? = null,

    var title: String? = null,

    var pubDate: String? = null,

    var category: String? = null
)