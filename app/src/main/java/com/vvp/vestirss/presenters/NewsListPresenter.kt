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
import javax.inject.Inject


@InjectViewState
class NewsListPresenter : MvpPresenter<NewsListView>() {

    //начальный массив для данных
    lateinit var newsList: ArrayList<NewsModel>

    @Inject
    lateinit var provider: DataProvider

    @Inject
    lateinit var methodsDAO: MethodsDAO


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        // инжектим переменные
        App.diComponent!!.injectNewsListPresenter(this)

        newsList = ArrayList()

        // при старте приложения сначала загружаем сохраненные новости из БД
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

                // если нет новостей в БД
                if (newsFromDB.isNullOrEmpty()) {

                    CoroutineScope(Dispatchers.Main).launch {

                        viewState.showProgress(false)
                        viewState.showTextViewMessage(R.string.empty_data_from_db)
                    }
                } else {
                    newsList.addAll(methodsDAO.getAllNews())

                    // реверс для правильного отображения по времени
                    newsList.reverse()

                    CoroutineScope(Dispatchers.Main).launch {
                        viewState.showProgress(false)
                        viewState.showNews(newsList = newsList, addNews = false)
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
                    viewState.showNews(newsList = newsList, addNews = false)
                    viewState.showButtonToolbar(true)
                }
            }

            // реверс для правильного отображения по времени
            newsFromNetwork.reverse()

            // Запись в БД
            if (!newsFromNetwork.isNullOrEmpty()) {
                methodsDAO.insertNewsList(newsList = newsFromNetwork)
            }
        }
    }


    // сортировка по категории
    fun sortingNewsList(selectedCategory: String, defaultCategory: String) {

        viewState.showProgress(true)

        // промежуточный массив для отсортированных данных
        val sortNewsList: ArrayList<NewsModel> = ArrayList()

        // если выбранная категория не равна категории "Все"
        if (selectedCategory != defaultCategory) {

            this.newsList.forEach {

                if (it.category.equals(selectedCategory)) {
                    sortNewsList.add(it)
                }
            }

            viewState.showProgress(false)

            // если нет новостей в выбранной категории
            if (sortNewsList.isEmpty()) {

                viewState.showNews(newsList = sortNewsList, addNews = false)
                viewState.showTextViewMessage("В категории $selectedCategory новостей еще нет")
            } else {
                viewState.showNews(newsList = sortNewsList, addNews = false)
            }

        } else {
            viewState.showProgress(false)
            viewState.showNews(newsList = newsList, addNews = false)
        }
    }


    // подгрузка новых новостей
    private fun loadNewData() {

        viewState.showProgress(true)

        CoroutineScope(Dispatchers.IO).launch {

            // новый массив с данными, загруженными из сети
            val loadedData = provider.getNewsList().await()

            if (!loadedData.isNullOrEmpty()) {

                // если первые заголовки совпадают - обновление списка не требуется
                if (loadedData[0].title == newsList[0].title) {

                    CoroutineScope(Dispatchers.Main).launch {
                        viewState.showProgress(false)
                        viewState.showMessage(R.string.no_new_data)
                    }
                } else {

                    // промежуточный массив для данных, отобранных как новые
                    val freshData: ArrayList<NewsModel> = ArrayList()

                    for (i in 0..loadedData.count()) {

                        // отбираем новые данные по дате публикации
                        if (loadedData[i].title != newsList[0].title) {

                            freshData.add(loadedData[i])
                        } else {

                            //когда проверка доходит до новости, являющейся первой в исходном массиве
                            break
                        }
                    }

                    freshData.reverse()

                    // обновление UI
                    CoroutineScope(Dispatchers.Main).launch {
                        viewState.showProgress(false)

                        //viewState.showNewsList(newsList)
                        viewState.showNews(newsList = freshData, addNews = true)

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
                viewState.showNews(newsList = newsList, addNews = false)
                viewState.showMessage(R.string.data_deleted_successfully)
                viewState.showTextViewMessage(R.string.empty_data_from_db)
                viewState.showButtonToolbar(false)
            }
        }
    }


}