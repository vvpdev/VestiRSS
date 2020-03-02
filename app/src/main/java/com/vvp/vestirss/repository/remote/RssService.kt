package com.vvp.vestirss.repository.remote

import okhttp3.ResponseBody
import retrofit2.http.GET

interface RssService {

    @GET("/vesti.rss")
    suspend fun getRssData(): ResponseBody
}