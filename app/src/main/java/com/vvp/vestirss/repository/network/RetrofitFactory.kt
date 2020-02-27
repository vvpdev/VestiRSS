package com.vvp.vestirss.repository.network

import retrofit2.Retrofit

class RetrofitFactory {

        private val BASE_URL = "https://www.vesti.ru"

        private fun getRetrofitInstance(): Retrofit{

                return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .build()
            }

        fun getRssService(): RssService = getRetrofitInstance().create(
            RssService::class.java)
}