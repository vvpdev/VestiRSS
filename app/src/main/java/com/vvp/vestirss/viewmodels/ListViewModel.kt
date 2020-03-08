package com.vvp.vestirss.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vvp.vestirss.App
import com.vvp.vestirss.R
import com.vvp.vestirss.repository.RepositoryClass
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import com.vvp.vestirss.utils.NewsListClass
import com.vvp.vestirss.utils.SingleLiveEvent
import kotlinx.coroutines.*
import javax.inject.Inject

class ListViewModel: ViewModel() {


    @Inject
    lateinit var repository: RepositoryClass

    // boolean наличия сохраненных новостей в БД
    var isSavedNews: MutableLiveData<Boolean> = MutableLiveData()

    // текущие новости, отображаемые на экране
    var newsList: MutableLiveData<NewsListClass> =  MutableLiveData()

    // boolean для показа toast
    var showLoading: MutableLiveData<Boolean> =  MutableLiveData()

    var messageStorage = SingleLiveEvent<Int>()


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

        loadFromDB()
    }


    // загрузка из БД
    private fun loadFromDB() {

        checkLoadDB()

        Log.i("VestiRSS_Log", "ListViewModel - loadFromDB()")

        showLoading.postValue(true)

        loadFromDBJob = CoroutineScope(Dispatchers.IO).launch {

            newsList.postValue(repository.loadFromDB() as NewsListClass?)
            showLoading.postValue(false)
        }
    }



    // проверка заполненности БД
    private fun checkLoadDB() {

        Log.i("VestiRSS_Log", "ListViewModel checkLoadDB()")

        CoroutineScope(Dispatchers.IO).launch {

            // запрос количества новостей в БД
            val quantity = repository.getSavedNewsQuantity()

            if (quantity != 0) {
                isSavedNews.postValue(true)         // есть сохраненные новости
            } else {
                isSavedNews.postValue(false)        // нет сохраненных новостей
            }
        }
    }


    // загрузка по свайпу
    fun separateLoad(){

        checkLoadDB()

        separateLoadJob = CoroutineScope(Dispatchers.IO).launch {

           showLoading.postValue(true)

            // если в БД нет сохраненных новостей
            if (isSavedNews.value == false) {

                if (repository.loadInitialData()){
                    newsList.postValue(repository.loadFromDB() as NewsListClass?)
                }
                showLoading.postValue(false)
                checkLoadDB()

            } else {

                if (repository.loadNewData()){                      // if return true
                    newsList.postValue(repository.loadFromDB() as NewsListClass?)    // возвращаем 10 свежих новостей
                    showLoading.postValue(false)
                    messageStorage.postValue(R.string.new_data_uploaded)
                } else{
                    showLoading.postValue(false)
                    messageStorage.postValue(R.string.no_new_data)
                }
            }
        }
    }



    // сортировка новостей по категориям
//    fun loadNewsFromCategory(category: String){
//        sortJob = CoroutineScope(Dispatchers.IO).launch {
//            newsList.postValue( repository.selectNewsForCategory(category = category)) }
//    }


    fun clearDB() {

        clearJob = CoroutineScope(Dispatchers.IO).launch {
            showLoading.postValue(true)
            repository.clearDB()
            delay(1000)
            checkLoadDB()
            showLoading.postValue(false)
            newsList.postValue(null)
            messageStorage.postValue(R.string.data_deleted_successfully)
        }
    }





    fun loadNextPage(currentIndex: Int, lastIndex: Int){

        val index: Int? = newsList.value?.newsList?.first()?.id

        Log.i("VestiRSS_Log", "ListViewModel loadNextPage()  переданный индекс = $index")

        loadPageJob = CoroutineScope(Dispatchers.IO).launch {

            val pagesList = index?.let { repository.loadPage(nextPage = true, index = it) }

            if (!pagesList.isNullOrEmpty()){
                newsList.postValue(NewsListClass(newsList = pagesList, currentNumber = (currentIndex + 1), lastNumber = lastIndex))
            }
            else{
                messageStorage.postValue(R.string.it_last_page)
            }
        }
    }


    fun loadBackPage(currentIndex: Int, lastIndex: Int){

        val index: Int? = newsList.value?.newsList?.last()?.id

        Log.i("VestiRSS_Log", "ListViewModel loadNextPage()  переданный индекс = $index")

        loadPageJob = CoroutineScope(Dispatchers.IO).launch {

            val pagesList = index?.let { repository.loadPage(nextPage = false, index = it) }

            if (!pagesList.isNullOrEmpty()){
                newsList.postValue( NewsListClass(newsList = pagesList, currentNumber = (currentIndex - 1), lastNumber = lastIndex) )
            }
            else{
                messageStorage.postValue(R.string.it_first_page)
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
