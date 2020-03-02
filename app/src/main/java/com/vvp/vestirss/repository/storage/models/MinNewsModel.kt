package com.vvp.vestirss.repository.storage.models

    //data class без дополнительных полей, которые не нужны для отображения в recyclerView

data class MinNewsModel (

    var title: String = "",

    var pubDate: String? = null,

    var category: String? = null
)