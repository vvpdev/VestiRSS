package com.vvp.vestirss.repository.network.retrofit

import android.util.Log
import com.vvp.vestirss.App
import com.vvp.vestirss.converters.DataConverter
import com.vvp.vestirss.repository.NewsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class DataProvider {

    @Inject
    lateinit var retrofitFactory: RetrofitFactory

    @Inject
    lateinit var converter: DataConverter

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

                    newsList.addAll(converter.convert(responseItems!!))
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