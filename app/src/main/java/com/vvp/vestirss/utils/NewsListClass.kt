package com.vvp.vestirss.utils

import com.vvp.vestirss.repository.storage.models.MinNewsModel

data class NewsListClass (

    var newsList: ArrayList<MinNewsModel>? = null,

    var currentNumber: Int? = null,

    var lastNumber: Int? = null
)