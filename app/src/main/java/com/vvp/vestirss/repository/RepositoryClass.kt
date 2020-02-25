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


    fun sizeNewsInDB(): Int{
        return methodsDAO.getAllNews().size
    }


    // загрузка из БД
    fun loadFromDB(): Deferred<ArrayList<NewsModel>> {

        return CoroutineScope(Dispatchers.IO).async {

            val loadFromDB: ArrayList<NewsModel> = ArrayList()
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

                    freshData.clear()
                    freshData.addAll(methodsDAO.getAllNews())
                }
            }
            newData.clear()

            return@async freshData
        }
    }


    // выбор новостей по категориям
    fun selectNewsForCategory(category: String): ArrayList<NewsModel>{

        // массив для отобранных по категории новостей
        val sortList: ArrayList<NewsModel> = ArrayList()

        CoroutineScope(Dispatchers.IO).launch {

            if (category != "Все"){
                sortList.addAll( methodsDAO.getNewsSelectedCategory(category = category) )
            } else {
                sortList.addAll( methodsDAO.getAllNews())
            }
        }
        return sortList
    }


    // получить новость по Id
    fun getNewsById(id: Int): Deferred<NewsModel>{
        return CoroutineScope(Dispatchers.IO).async {
            return@async methodsDAO.getNewsById(id = id)
        }
    }

}