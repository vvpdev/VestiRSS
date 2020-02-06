package com.vvp.vestirss.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.vvp.vestirss.App
import com.vvp.vestirss.R
import com.vvp.vestirss.repository.NewsModel
import com.vvp.vestirss.repository.datebase.MethodsDAO
import com.vvp.vestirss.repository.network.retrofit.DataProvider
import com.vvp.vestirss.views.NewsListView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


@Suppress("DEPRECATION")
@InjectViewState
class NewsListPresenter: MvpPresenter<NewsListView>() {

    //начальный массив для данных
    lateinit var newsList: LinkedList<NewsModel>

    @Inject
    lateinit var provider: DataProvider

    @Inject
    lateinit var methodsDAO: MethodsDAO


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        // инжектим переменные
        App.diComponent!!.injectNewsListPresenter(this)

        newsList = LinkedList()

        // при старте приложения сначала загружаем сохраненные данные из БД
        loadDataFromDB()
    }


    // для свайпа
    // разделение функций загрузки - начальная или для добавления новых данных
    fun selectionLoad() {
        if (newsList.isNullOrEmpty()) {
            loadInitialData()
        } else {
            loadNewData()
        }
    }



    // загрузка из БД
    private fun loadDataFromDB() {

        viewState.showProgress(true)

        if (newsList.isNullOrEmpty()) {

            CoroutineScope(Dispatchers.IO).launch {

                val newsFromDB = methodsDAO.getAllNews()

                // если нет данных в БД
                if (newsFromDB.isNullOrEmpty()) {

                    CoroutineScope(Dispatchers.Main).launch {

                        viewState.showProgress(false)
                        viewState.showTextViewMessage(R.string.empty_data_from_db)
                    }
                } else {
                    newsList.addAll( methodsDAO.getAllNews() )

                    // т.к. элементы при записи добавляются в конец таблицы
                    // реверс при загрузке из БД выводит новые элементы на первые позиции

                    newsList.reverse()

                    CoroutineScope(Dispatchers.Main).launch {
                        viewState.showProgress(false)
                        viewState.showNewsList(newsList)
                    }
                }
            }
        }
    }



    // изначальная загрузка новостей из сети
    private fun loadInitialData() {

        viewState.showProgress(true)

        CoroutineScope(Dispatchers.IO).launch {

            // данные из сети
            val newsFromNetwork = provider.getNewsList().await()
            newsList.addAll(newsFromNetwork)

            CoroutineScope(Dispatchers.Main).launch {

                viewState.showProgress(false)

                if (newsList.isNullOrEmpty()) {
                    viewState.showMessage(R.string.error_load_news_list)
                } else {
                    viewState.showNewsList(newsList)
                    viewState.showButtonToolbar(true)
                }
            }

            // Запись в БД
            if (!newsFromNetwork.isNullOrEmpty()) {

                newsFromNetwork.reverse()
                methodsDAO.insertNewsList(newsList = newsFromNetwork)
            }
        }
    }


    // сортировка по категории
    fun sortingNewsList(selectedCategory: String, defaultCategory: String) {

        viewState.showProgress(true)

        // промежуточный массив для отсортированных данных
        val sortNewsList: LinkedList<NewsModel> = LinkedList()

        if (selectedCategory != defaultCategory) {

            this.newsList.forEach {

                if (it.category.equals(selectedCategory)) {
                    sortNewsList.add(it)
                }
            }

            viewState.showProgress(false)

            // если нет новостей в выбранной категории
            if (sortNewsList.isEmpty()) {

                viewState.showNewsList(sortNewsList)
                viewState.showTextViewMessage("В категории $selectedCategory новостей еще нет")
            } else {
                viewState.showNewsList(sortNewsList)
            }

        } else {
            viewState.showProgress(false)
            viewState.showNewsList(newsList)
        }
    }



    // подгрузка новых новостей
    private fun loadNewData() {

        viewState.showProgress(true)

        CoroutineScope(Dispatchers.IO).launch {

            // новый массив с данными, загруженными из сети
            val loadedData = provider.getNewsList().await()

            if (!loadedData.isNullOrEmpty()) {

                // конвертирование даты первой новости массива, отображенного в recyclerView
                val firstPubDateInt = (((Date(newsList.first.pubDate)).time) / 1000).toDouble().roundToInt()

                // конвертирование даты первой новости массива, загруженного из сети
                val newFirstPubDateInt = ((((Date(loadedData[0].pubDate)).time) / 1000).toDouble().roundToInt())

                // если первые даты совпадают - обновление списка не требуется
                if (newFirstPubDateInt == firstPubDateInt) {

                    CoroutineScope(Dispatchers.Main).launch {
                        viewState.showProgress(false)
                        viewState.showMessage(R.string.no_new_data)
                    }
                } else {

                    // промежуточный массив для данных, отобранных как новые
                    val freshData: ArrayList<NewsModel> = ArrayList()

                    for (i in 0..loadedData.size) {

                        // отбираем новые данные по дате публикации
                        if ( ((((Date(loadedData[i].pubDate)).time) / 1000).toDouble().roundToInt()) >  firstPubDateInt ) {

                            freshData.add(loadedData[i])
                        }
                        else{

                            //когда проверка доходит до новости, являющейся первой в исходном массиве
                            break
                        }
                    }

                    freshData.reverse()

                    // загружаем на экран в соответствии с датой  - самые новые новости будут вверху списка
                    freshData.forEach {
                        newsList.addFirst(it)
                    }

                    // обновление UI
                    CoroutineScope(Dispatchers.Main).launch {
                        viewState.showProgress(false)
                        viewState.showNewsList(newsList)
                        viewState.showMessage(R.string.new_data_uploaded)
                    }

                    // запись в БД
                    methodsDAO.insertNewsList(freshData)
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    viewState.showProgress(false)
                    viewState.showMessage(R.string.error_load_news_list)
                }
            }
        }
    }


    // очистка БД
    fun clearDateBase() {

        viewState.showProgress(true)

        CoroutineScope(Dispatchers.IO).launch {

            newsList.clear()
            methodsDAO.deleteAllNews()

            CoroutineScope(Dispatchers.Main).launch {
                viewState.showProgress(false)
                viewState.showNewsList(newsList)
                viewState.showMessage(R.string.data_deleted_successfully)
                viewState.showTextViewMessage(R.string.empty_data_from_db)
                viewState.showButtonToolbar(false)
            }
        }
    }



}