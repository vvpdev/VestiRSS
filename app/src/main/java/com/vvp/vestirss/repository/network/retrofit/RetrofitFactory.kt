@file:Suppress("DEPRECATION")

package com.vvp.vestirss.repository.network.retrofit

import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

@Suppress("DEPRECATION")
class RetrofitFactory {

        private val BASE_URL = "https://www.vesti.ru/"

        private fun getRetrofitInstance(): Retrofit{

                return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build()
            }

        fun getRssService(): RssService = getRetrofitInstance().create(
            RssService::class.java)
}