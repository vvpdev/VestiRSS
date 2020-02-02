package com.vvp.vestirss.repository.network.retrofit

import android.util.Log
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.NewsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class DataProvider {

    @Inject
    lateinit var retrofitFactory: RetrofitFactory

    init {
        App.diComponent!!.injectDataProvider(this)
    }


    // загрузка новостей
    suspend fun getNewsList(): Deferred<ArrayList<NewsModel>>{

        return CoroutineScope(Dispatchers.IO).async {

            // массив для возвращаемых данных
            val newsList: ArrayList<NewsModel> = ArrayList()

            try {
                // если запрос выполнен удачно
                if (retrofitFactory.getRssService().getRssData().isSuccessful){

                    val responseItems = retrofitFactory.getRssService().getRssData().body()!!.channel!!.items

                    // передаем данные в класс - модель
                    responseItems!!.forEach { newsList.add(

                        NewsModel(
                            title = it.title,
                            pubDate = it.pubDate!!,
                            category = it.category,
                            imageUrl = it.enclosure!![0].url,
                            fullText = it.yandexFullText
                        )
                    )
                    }
                }

                else{
                    Log.i("DataProvider", "response is fail")
                }
            }

            catch (e: Exception){
                Log.i("DataProvider", "network is not available")
            }

            return@async newsList
        }
    }

}