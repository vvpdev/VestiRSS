package com.vvp.vestirss.repository

import com.vvp.vestirss.App
import com.vvp.vestirss.repository.storage.MethodsDAO
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import com.vvp.vestirss.repository.storage.models.NewsModel
import com.vvp.vestirss.repository.remote.DataProvider
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


    fun sizeNewsInDB(): Int{
        return methodsDAO.getAllMinNews().size
    }


    // загрузка из БД
    suspend fun loadFromDB(): ArrayList<MinNewsModel> {

        return CoroutineScope(Dispatchers.IO).async {

            val loadFromDB: ArrayList<MinNewsModel> = ArrayList()
            loadFromDB. addAll( methodsDAO.getAllMinNews() )
            return@async loadFromDB
        }.await()
    }


    // удаление всех новостей из БД
    fun clearDB(){
        CoroutineScope(Dispatchers.IO).launch {
            methodsDAO.deleteAllNews()
        }
    }


    // начальное получение данных из сети и запись в БД
    suspend fun loadInitialData(): ArrayList<MinNewsModel> {

        return CoroutineScope(Dispatchers.IO).async {

            val initialNewsList: ArrayList<NewsModel> = ArrayList()
            val initialMinNewsList: ArrayList<MinNewsModel> = ArrayList()
            initialNewsList.addAll( provider.getNewsList()  )

            if (!initialNewsList.isNullOrEmpty()) {

                // сортировка по возрастанию времени
                initialNewsList.sortBy { it.pubDate }

                methodsDAO.insertNewsList(newsList = initialNewsList)

                initialMinNewsList.addAll(methodsDAO.getAllMinNews())
            }
            return@async initialMinNewsList
        }.await()
    }


    // подгрузка новых данных
    suspend fun loadNewData(): Boolean {

        // промежуточный массив для данных, отобранных как новые
        val freshData: ArrayList<NewsModel> = ArrayList()

        return CoroutineScope(Dispatchers.IO).async {

            val lastTitle = methodsDAO.getAllMinNews().last().title

            val newData = provider.getNewsList()

            if (!newData.isNullOrEmpty()){

                // проверка заголовков на совпадение
                if (lastTitle != newData[0].title) {

                    for (i in 0..newData.lastIndex) {
                        if (newData[i].title != lastTitle) {
                            freshData.add(newData[i])
                        } else {
                            // прекращаем проверку
                            break
                        }
                    }

                    // записываем в БД (сортировка по возрастанию времени)
                    freshData.sortBy { it.pubDate }
                    methodsDAO.insertNewsList(newsList = freshData)
                } else{
                    return@async false
                }
            }
            newData.clear()

            return@async true
        }.await()
    }


    // выбор новостей по категориям
    fun selectNewsForCategory(category: String): ArrayList<MinNewsModel>{

        // массив для отобранных по категории новостей
        val sortList: ArrayList<MinNewsModel> = ArrayList()

        CoroutineScope(Dispatchers.IO).launch {

            if (category != "Все"){
                sortList.addAll( methodsDAO.getAllMinNews(category = category) )
            } else {
                sortList.addAll( methodsDAO.getAllMinNews())
            }
        }
        return sortList
    }


    // получить новость по Id
    suspend fun getNewsByTitle(title: String): NewsModel {
        return CoroutineScope(Dispatchers.IO).async {
            return@async methodsDAO.getNewsByTitle(title = title)
        }.await()
    }

}