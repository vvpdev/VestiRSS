package com.vvp.vestirss.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.RepositoryClass
import com.vvp.vestirss.utils.NewsListStates
import kotlinx.coroutines.*
import javax.inject.Inject

class NewsListViewModel: ViewModel() {


    @Inject
    lateinit var repository: RepositoryClass

    // liveData наличия сохраненных новостей в БД
    var isSavedNews: MutableLiveData<Boolean> = MutableLiveData()

    // liveData для текущих состояний
    var statesList: MutableLiveData<NewsListStates> =  MutableLiveData()

    // liveData для прогресса загрузки
    var showLoading: MutableLiveData<Boolean> =  MutableLiveData()


    // для отмены корутин
    private var job: Job? = null


    // инжектирование репозитория
    init {
        App.diComponent?.injectNewsListViewModel(viewModel = this)
    }


    // загрузка из БД
    fun loadFromDB() {
        job = CoroutineScope(Dispatchers.IO).launch {
            statesList.postValue(repository.loadFromDB().await().let { NewsListStates.LoadedFromDBState(newsList = it) })
            checkLoadDB()
        }
    }


    // проверка заполненности БД
    private fun checkLoadDB() {

        job = CoroutineScope(Dispatchers.IO).launch {
            if (repository.sizeNewsInDB() != 0) {
                isSavedNews.postValue(true)
            } else {
                isSavedNews.postValue(false)
            }
        }
    }


    // загрузка по свайпу
    fun separateLoad(){

        job = CoroutineScope(Dispatchers.IO).launch {

           showLoading.postValue(true)

            // если в БД нет сохраненных новостей
            if (repository.sizeNewsInDB() == 0) {
                statesList.postValue( NewsListStates.InitLoadFromNetworkState( newsList = repository.loadInitialData().await()) )
                showLoading.postValue(false)
                checkLoadDB()

            } else {

                if (repository.loadNewData().await()){
                    statesList.postValue( NewsListStates.LoadNewDataState(newsList = repository.loadFromDB().await()) )
                    showLoading.postValue(false)
                }
            }
        }
    }


    // сортировка новостей по категориям
    fun loadNewsFromCategory(category: String){
        job = CoroutineScope(Dispatchers.IO).launch {
            statesList.postValue( NewsListStates.SortState(newsList = repository.selectNewsForCategory(category = category)) ) }
    }



    fun clearDB() {

        job = CoroutineScope(Dispatchers.IO).launch {
            showLoading.postValue(true)
            repository.clearDB()
            delay(500)
            checkLoadDB()
            showLoading.postValue(false)
            statesList.postValue(NewsListStates.EmptyDBState)
        }
    }


    fun onDestroy(){
        job?.cancel()
    }
}
