package com.vvp.vestirss.repository.network.retrofit

import com.vvp.vestirss.repository.network.xml_models.Rss
import retrofit2.Response
import retrofit2.http.GET

interface RssService {


    @GET("/vesti.rss")
    suspend fun getRssData(): Response<Rss>
}