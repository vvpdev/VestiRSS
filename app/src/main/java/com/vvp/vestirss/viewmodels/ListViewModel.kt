package com.vvp.vestirss.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.RepositoryClass
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import kotlinx.coroutines.*
import javax.inject.Inject

class ListViewModel: ViewModel() {


    @Inject
    lateinit var repository: RepositoryClass

    // liveData наличия сохраненных новостей в БД
    var isSavedNews: MutableLiveData<Boolean> = MutableLiveData()

    // liveData для текущих состояний
    var newsList: MutableLiveData<ArrayList<MinNewsModel>> =  MutableLiveData()

    // liveData для прогресса загрузки
    var showLoading: MutableLiveData<Boolean> =  MutableLiveData()


    // для отмены корутин
    private var loadFromDBJob: Job? = null
    private var checkLoadDBJob: Job? = null
    private var separateLoadJob: Job? = null
    private var sortJob: Job? = null
    private var clearJob: Job? = null
    private var loadPageJob: Job? = null





    // инжектирование репозитория
    init {
        App.diComponent?.injectNewsListViewModel(viewModel = this)
    }


    // загрузка из БД
    fun loadFromDB() {
        loadFromDBJob = CoroutineScope(Dispatchers.IO).launch {

            showLoading.postValue(true)
            newsList.postValue( repository.loadFromDB() )
            checkLoadDB()
            showLoading.postValue(false)
        }
    }



    // проверка заполненности БД
    private fun checkLoadDB() {

        checkLoadDBJob = CoroutineScope(Dispatchers.IO).launch {
            if (repository.sizeNewsInDB() != 0) {
                isSavedNews.postValue(true)
            } else {
                isSavedNews.postValue(false)
            }
        }
    }


    // загрузка по свайпу
    fun separateLoad(){

        separateLoadJob = CoroutineScope(Dispatchers.IO).launch {

           showLoading.postValue(true)

            // если в БД нет сохраненных новостей
            if (repository.sizeNewsInDB() == 0) {
                newsList.postValue( repository.loadInitialData() )
                showLoading.postValue(false)
                checkLoadDB()

            } else {

                if (repository.loadNewData()){
                    newsList.postValue( repository.loadFromDB() )
                    showLoading.postValue(false)
                } else{
                    showLoading.postValue(false)
                }
            }
        }
    }


    // сортировка новостей по категориям
    fun loadNewsFromCategory(category: String){
        sortJob = CoroutineScope(Dispatchers.IO).launch {
            newsList.postValue( repository.selectNewsForCategory(category = category)) }
    }



    fun clearDB() {

        clearJob = CoroutineScope(Dispatchers.IO).launch {
            showLoading.postValue(true)
            repository.clearDB()
            delay(500)
            checkLoadDB()
            showLoading.postValue(false)
            newsList.postValue(null)
        }
    }





    fun loadNextPage(){

        val index: Int? = newsList.value?.last()?.id

        loadPageJob = CoroutineScope(Dispatchers.IO).launch {

            val pagesList = index?.let { repository.loadPage(nextPage = true, index = it) }

            if (!pagesList.isNullOrEmpty()){
                newsList.postValue( pagesList )
            }
            else{
                Log.i("VestiRssLog", "loadNextPage  пустой массив")
            }
        }
    }


    fun loadBackPage(){

        val index: Int? = newsList.value?.first()?.id

        loadPageJob = CoroutineScope(Dispatchers.IO).launch {

            val pagesList = index?.let { repository.loadPage(nextPage = false, index = it) }

            if (!pagesList.isNullOrEmpty()){
                newsList.postValue(pagesList)
            } else {
                Log.i("VestiRssLog", "loadBackPage  пустой массив")
            }
        }
    }



    fun onDestroy(){

        loadFromDBJob?.cancel()
        checkLoadDBJob?.cancel()
        separateLoadJob?.cancel()
        sortJob?.cancel()
        clearJob?.cancel()
        loadPageJob?.cancel()
    }
}
