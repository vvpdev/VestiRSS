package com.vvp.vestirss.repository

import android.util.Log
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.remote.DataProvider
import com.vvp.vestirss.repository.storage.MethodsDAO
import com.vvp.vestirss.repository.storage.models.LastIndex
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import com.vvp.vestirss.repository.storage.models.NewsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
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

            val lastIndex: LastIndex = methodsDAO.getLastIndexById(id = 1)

            try {
                for (i in (lastIndex.lastIndex - 9) .. lastIndex.lastIndex){
                    loadFromDB.add(methodsDAO.getMinNewsById(id = i))
                }
            }
            catch (e: Exception){

            }

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

            if (initialNewsList.isNotEmpty()) {

                // сортировка по возрастанию времени
                initialNewsList.sortBy { it.pubDate }

                val longArray: LongArray = methodsDAO.insertNewsList(newsList = initialNewsList)

                // индекс самой последней новости
                val lastIndex: Int = longArray[longArray.lastIndex].toInt()

                methodsDAO.insertLastIndex(LastIndex(id = 1, lastIndex = lastIndex))


                for (i in (lastIndex - 10) .. lastIndex){
                    initialMinNewsList.add(methodsDAO.getMinNewsById(id = i))
                }
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

                    val longArray: LongArray = methodsDAO.insertNewsList(newsList = freshData)

                    val lastIndex: Int = longArray[longArray.lastIndex].toInt()

                    methodsDAO.insertLastIndex(LastIndex(id = 1, lastIndex = lastIndex))

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
    suspend fun getNewsById(id: Int): NewsModel {
        return CoroutineScope(Dispatchers.IO).async {
            return@async methodsDAO.getNewsById(id = id)
        }.await()
    }


    // переход по страницам
    suspend fun loadPage(nextPage: Boolean, index: Int): ArrayList<MinNewsModel> {

        return CoroutineScope(Dispatchers.IO).async {

            val lastIndex: Int = methodsDAO.getLastIndexById(id = 1).lastIndex

            val newsList: ArrayList<MinNewsModel> = ArrayList()

            if (nextPage) {

                for (i in (index + 1) until (index +11)) {

                    if (i != (lastIndex + 1)) {

                        newsList.add(methodsDAO.getMinNewsById(id = i))
                    }
                    else {
                        break
                    }
                }
            }

            else {

                if (index >= 10) {
                    for (i in (index - 10) until index) {

                        if (i != 0){
                            newsList.add(methodsDAO.getMinNewsById(id = i))
                        }
                    }
                }
                else{

                    for (i in 1 until index){
                        newsList.add(methodsDAO.getMinNewsById(id = i))
                    }
                }
            }
            return@async newsList

        }.await()
    }




}