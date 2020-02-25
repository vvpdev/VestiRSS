package com.vvp.vestirss.repository

import android.util.Log
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


    suspend fun sizeNewsInDB(): Int{
        return methodsDAO.getAllNews().size
    }


    // загрузка из БД
    fun loadFromDB(): Deferred<ArrayList<NewsModel>> {

        val loadFromDB: ArrayList<NewsModel> = ArrayList()

        return CoroutineScope(Dispatchers.IO).async {
            loadFromDB. addAll( methodsDAO.getAllNews() )
            return@async loadFromDB
        }
    }


    // удаление всех новостей из БД
    fun clearDB(){
        CoroutineScope(Dispatchers.IO).launch {
            methodsDAO.deleteAllNews()
        }
    }



    // начальное получение данных из сети и запись в БД
     fun loadInitialData(): Deferred<ArrayList<NewsModel>> {

        return CoroutineScope(Dispatchers.IO).async {

            val initialNewsList: ArrayList<NewsModel> = ArrayList()
            initialNewsList.addAll( provider.getNewsList().await()  )

            if (!initialNewsList.isNullOrEmpty()) {

                // сортировка по возрастанию времени
                initialNewsList.sortBy { it.pubDate }

                methodsDAO.insertNewsList(newsList = initialNewsList)
                Log.i("VestiRSS_Log", "RepositoryClass (loadInitialData) записано в БД = ${initialNewsList.size}")

                initialNewsList.clear()
                initialNewsList.addAll( methodsDAO.getAllNews() )
            }
            return@async initialNewsList
        }
    }



    // подгрузка новых данных
    fun loadNewData(): Deferred<ArrayList<NewsModel>> {

        // промежуточный массив для данных, отобранных как новые
        val freshData: ArrayList<NewsModel> = ArrayList()

        return CoroutineScope(Dispatchers.IO).async {

            val lastTitle = methodsDAO.getAllNews().last().title

            val newData = provider.getNewsList().await()

            if (newData.isNotEmpty()){

                // проверка заголовков на совпадение
                if (lastTitle == newData[0].title) {

                    Log.i("VestiRSS_Log", "новых новостей нет")
                } else {
                    for (i in 0..newData.lastIndex) {
                        if (newData[i].title != lastTitle) {
                            freshData.add(newData[i])
                        } else {
                            // прекращаем проверку
                            break
                        }
                    }

                    Log.i("VestiRSS_Log", "количество новых элементов = ${freshData.size}")

                    // записываем в БД (сортировка по возрастанию времени)
                    freshData.sortBy { it.pubDate }
                    methodsDAO.insertNewsList(newsList = freshData)
                    Log.i("VestiRSS_Log", "RepositoryClass (loadNewData) записано в БД  = ${freshData.size}")

                    freshData.clear()
                    freshData.addAll(  methodsDAO.getAllNews() )

                    Log.i("VestiRSS_Log", "RepositoryClass (loadNewData) получено из БД  = ${freshData.size}")
                }
            }

            return@async freshData
        }
    }


    fun selectNewsForCategory(category: String): ArrayList<NewsModel>{

        // массив для отобранных по категории новостей
        val sortList: ArrayList<NewsModel> = ArrayList()

        CoroutineScope(Dispatchers.IO).launch {

            if (category != "Все"){
                sortList.addAll( methodsDAO.getNewsSelectedCategory(category = category) )

                Log.i("VestiRSS_Log", "RepositoryClass (selectNewsForCategory) количество в категории = ${sortList.size} ")
            } else {
                sortList.addAll( methodsDAO.getAllNews())
            }
        }

        return sortList
    }
}