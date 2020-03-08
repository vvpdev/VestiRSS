package com.vvp.vestirss.repository

import android.util.Log
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.remote.DataProvider
import com.vvp.vestirss.repository.storage.MethodsDAO
import com.vvp.vestirss.repository.storage.tools.LastIndex
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import com.vvp.vestirss.repository.storage.models.NewsModel
import com.vvp.vestirss.repository.storage.tools.NewsQuantity
import com.vvp.vestirss.utils.NewsListClass
import kotlinx.coroutines.*
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



    // количество новостей в БД на данный момент
    suspend fun getSavedNewsQuantity(): Int {

        return CoroutineScope(Dispatchers.IO).async {
             try {
                val quantity = methodsDAO.getQuantityById(id = 1).quantity

                Log.i("VestiRSS_Log", "RepositoryClass getSavedNewsQuantity количество новостей в БД = $quantity")

                 return@async quantity

            } catch (e: Exception){
                0
            }
        }.await()
    }


    // начальная загрузка из БД
    suspend fun loadFromDB(): Any {

        return CoroutineScope(Dispatchers.IO).async {

            val loadFromDB: ArrayList<MinNewsModel> = ArrayList()

            try {

                val quantityNews = getSavedNewsQuantity()

                if (quantityNews != 0){

                    val lastIndex: LastIndex = methodsDAO.getLastIndexById(id = 1)

                    Log.i("VestiRSS_Log", "RepositoryClass loadFromDB()  lastIndex = ${lastIndex.lastIndex}")

                    for (i in (lastIndex.lastIndex - 9) .. lastIndex.lastIndex){
                        loadFromDB.add(methodsDAO.getMinNewsById(id = i))
                    }

                    if ((quantityNews % 10) != 0){
                        return@async NewsListClass(newsList = loadFromDB, currentNumber = 1, lastNumber = ((quantityNews / 10) + 1))
                    }
                    else{
                        return@async NewsListClass(newsList = loadFromDB, currentNumber = 1, lastNumber = (quantityNews / 10))
                    }
                }
                else{
                    return@async NewsListClass(newsList = null, currentNumber = 0, lastNumber = 0)
                }
            }
            catch (e: Exception){
                return@async NewsListClass(newsList = null, currentNumber = 0, lastNumber = 0)
            }
        }.await()
    }



    // удаление всех новостей из БД
    fun clearDB(){
        CoroutineScope(Dispatchers.IO).launch {
            methodsDAO.deleteAllNews()
            methodsDAO.insertQuantity(NewsQuantity(id = 1, quantity = 0))   // сброс количества

            getSavedNewsQuantity()
        }
    }


    // начальное получение данных из сети и запись в БД
    suspend fun loadInitialData(): Boolean {

        return CoroutineScope(Dispatchers.IO).async {

            val initialNewsList: ArrayList<NewsModel> = ArrayList()
            initialNewsList.addAll( provider.getNewsList()  )

            if (initialNewsList.isNotEmpty()) {

                // сортировка по возрастанию времени
                initialNewsList.sortBy { it.pubDate }

                // insert возвращает массив индексов записанных новостей
                val longArray: LongArray = methodsDAO.insertNewsList(newsList = initialNewsList)

                // фиксируем количество сохраненных новостей
                methodsDAO.insertQuantity(NewsQuantity(id = 1, quantity = longArray.size))

                // фиксируем индекс самой "свежей" сохраненной новости
                val lastIndex: Int = longArray[longArray.lastIndex].toInt()
                methodsDAO.insertLastIndex( LastIndex(id = 1, lastIndex = lastIndex ))

                return@async true
            } else{
                return@async false
            }

        }.await()
    }




    // подгрузка новых данных
    suspend fun loadNewData(): Boolean {        // boolean - есть ли новые новости

        // промежуточный массив для данных, отобранных как новые
        val freshData: ArrayList<NewsModel> = ArrayList()

        return CoroutineScope(Dispatchers.IO).async {

            // индекс последней новости
            val lastIDNews = methodsDAO.getLastIndexById(id = 1).lastIndex

            // title последней новости
            val lastTitle = methodsDAO.getMinNewsById(id = lastIDNews).title

            // получаем новые данные
            val newData = provider.getNewsList()

            if (!newData.isNullOrEmpty()){

                // проверка заголовков на совпадение
                if (lastTitle != newData[0].title) {

                    for (i in 0..newData.lastIndex) {
                        if (newData[i].title != lastTitle) {    // проверка по заголовкам до совпадения
                            freshData.add(newData[i])
                        } else {
                            // прекращаем проверку
                            break
                        }
                    }

                    // записываем в БД (сортировка по возрастанию времени)
                    freshData.sortBy { it.pubDate }

                    val longArray: LongArray = methodsDAO.insertNewsList(newsList = freshData)

                    // обновляем индекс последней новости
                    val lastIndex: Int = longArray[longArray.lastIndex].toInt()
                    methodsDAO.insertLastIndex( LastIndex(id = 1, lastIndex = lastIndex ))

                    // обновляем количество сохраненных новостей в БД
                    val oldQuantity = methodsDAO.getQuantityById(id = 1).quantity
                    methodsDAO.insertQuantity(NewsQuantity(id = 1, quantity = (oldQuantity + freshData.size)))

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
              //  sortList.addAll( loadFromDB() )
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
            Log.i("VestiRSS_Log", "RepositoryClass loadPage lastIndex= $lastIndex")

            val quantity = getSavedNewsQuantity()

            val newsList: ArrayList<MinNewsModel> = ArrayList()

            when(nextPage){

                // листание вперед - next
                true -> {

                    var i: Int = index -1

                    while (newsList.size != 10 && i != (lastIndex - quantity) ){
                        newsList.add(methodsDAO.getMinNewsById(id = i))
                        i -= 1
                    }

                    newsList.sortBy { it.pubDate }
                }


                // листание назад - back
                false -> {

                    if (index != (lastIndex + 1)){

                        var i: Int = index + 1

                        while (newsList.size != 10 && i != (lastIndex + 1))  {
                            newsList.add(methodsDAO.getMinNewsById(id = i))
                            i++
                        }

                        newsList.sortBy { it.pubDate }
                    }
                }

            }
            return@async newsList
        }.await()
    }
}