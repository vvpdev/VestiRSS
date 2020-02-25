package com.vvp.vestirss.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.RepositoryClass
import com.vvp.vestirss.repository.models.NewsModel
import com.vvp.vestirss.utils.NewsListStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewsListViewModel: ViewModel() {


    @Inject
    lateinit var repository: RepositoryClass

    // индикатор наличия сохраненных новостей в БД
    val isSavedNews: MutableLiveData<Boolean> = MutableLiveData()

    // хранилище liveData для текущих состояний
    val newsListState: MutableLiveData<NewsListStates> =  MutableLiveData()

    // инжектирование репозитория
    init {
        App.diComponent!!.injectNewsListViewModel(viewModel = this)
    }


    // загрузка из БД
    fun loadFromDB() {
        CoroutineScope(Dispatchers.IO).launch {
            newsListState.postValue( NewsListStates.LoadedFromDBState(newsList = repository.loadFromDB().await()) )
            checkLoadDB()
        }
    }


    // проверка заполненности БД
    private fun checkLoadDB() {

        CoroutineScope(Dispatchers.IO).launch {
            if (repository.sizeNewsInDB() != 0) {
                isSavedNews.postValue(true)
            } else {
                isSavedNews.postValue(false)
            }
        }
    }


    // загрузка по свайпу
    fun separateLoad(){

        newsListState.postValue( NewsListStates.LoadingState )

        CoroutineScope(Dispatchers.IO).launch {

            // если в БД нет сохраненных новостей
            if (repository.sizeNewsInDB() == 0) {

                Log.i("VestiRSS_Log", "NewsListViewModel запрос метода loadInitialData()")

                newsListState.postValue( NewsListStates.InitLoadFromNetworkState( newsList =  repository.loadInitialData().await()) )
                checkLoadDB()

            } else {

                Log.i("VestiRSS_Log", "NewsListViewModel запрос метода loadNewData()")

                val freshData: ArrayList<NewsModel> = repository.loadNewData().await()

                if (freshData.isNullOrEmpty()){

                    // если новых данных нет, возвращаем данные из БД
                    newsListState.postValue(NewsListStates.LoadNewDataState(newsList = repository.loadFromDB().await(), isNew = false) )
                } else {

                    // если есть новые данные
                    newsListState.postValue( NewsListStates.LoadNewDataState(newsList = freshData, isNew = true) )
                    checkLoadDB()
                }
            }
        }
    }


    // сортировка новостей по категориям
    fun loadNewsFromCategory(category: String){
            CoroutineScope(Dispatchers.IO).launch {

                newsListState.postValue( NewsListStates.SortState(newsList = repository.selectNewsForCategory(category = category)) )
            }
    }


    // очистка БД и кеша
    fun clearDB() {

        CoroutineScope(Dispatchers.IO).launch {
            newsListState.postValue(NewsListStates.LoadingState)
            repository.clearDB()
            delay(500)
            checkLoadDB()
            newsListState.postValue(NewsListStates.EmptyDBState)
        }
    }
}
