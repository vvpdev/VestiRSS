package com.vvp.vestirss.repository

import com.vvp.vestirss.App
import com.vvp.vestirss.repository.datebase.MethodsDAO
import com.vvp.vestirss.repository.models.NewsModel
import com.vvp.vestirss.repository.network.retrofit.DataProvider
import kotlinx.coroutines.*
import javax.inject.Inject

class RepositoryClass {

    @Inject
    lateinit var provider: DataProvider

    @Inject
    lateinit var methodsDAO: MethodsDAO

    init {
        App.diComponent!!.injectRepositoryClass(this)
    }



    // загрузка с БД
    fun loadFromDB(): List<NewsModel> {

        val newsFromDB: ArrayList<NewsModel> = ArrayList()

        CoroutineScope(Dispatchers.IO).launch {
            newsFromDB.addAll( methodsDAO.getAllNews() )

            if (newsFromDB.isNotEmpty()){
                // сортировка по дате самые свежие новости вверху списка
                newsFromDB.sortBy { it.pubDate }
            }
        }
        return newsFromDB
    }



    // удаление всех новостей из БД
    fun clearDB(){
        CoroutineScope(Dispatchers.IO).launch {
            methodsDAO.deleteAllNews()
        }
    }



    // запись в БД
    fun writeToDB(news: ArrayList<NewsModel>){

        CoroutineScope(Dispatchers.IO).launch {

            if (news.isNotEmpty()){
                methodsDAO.insertNewsList(newsList = news)
            }
        }
    }



    // первоначальное получение данных
    fun loadFromNetwork(): Deferred<ArrayList<NewsModel>>{

        val initialNewsList: ArrayList<NewsModel> = ArrayList()

        return CoroutineScope(Dispatchers.IO).async {

            initialNewsList.addAll( provider.getNewsList().await() )

            initialNewsList.sortByDescending { it.pubDate }

            return@async initialNewsList
        }
    }


    // подгрузка новых данных
//    fun loadNewData(): Deferred<ArrayList<NewsModel>> {
//
//        // промежуточный массив для данных, отобранных как новые
//        val freshData: ArrayList<NewsModel> = ArrayList()
//
//        return CoroutineScope(Dispatchers.IO).async {
//
//            // новый массив с данными, загруженными из сети
//            val loadedData = provider.getNewsList().await()
//
//            if (!loadedData.isNullOrEmpty()) {
//
//                // если время постов совпадает - обновления не требуется
//                if (loadedData[0].pubDate != lastTime) {
//
//                    for (i in 0..loadedData.count()) {
//
//                        // отбираем новые данные по дате публикации
//                        if (loadedData[i].pubDate != lastTime) {
//
//                            freshData.add(loadedData[i])
//                        } else {
//
//                            //когда проверка доходит до новости, являющейся первой в исходном массиве
//                            break
//                        }
//                    }
//
//                    // запись в БД
//                    methodsDAO.insertNewsList(newsList = freshData)
//                }
//            }
//            return@async freshData
//        }
//    }

}