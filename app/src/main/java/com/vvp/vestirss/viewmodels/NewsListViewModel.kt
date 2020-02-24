package com.vvp.vestirss.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.models.NewsModel
import com.vvp.vestirss.repository.RepositoryClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewsListViewModel: ViewModel() {


    @Inject
    lateinit var repository: RepositoryClass


    // индикатор загрузки
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    // массив liveData для новостей
    val newsList: MutableLiveData<ArrayList<NewsModel>> =  MutableLiveData()


    // инжектирование репозитория
    init {
        App.diComponent!!.injectNewsListViewModel(this)
    }


    // загрузка новостей
    fun updateNewsList(){

        isLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {

            val newData = repository.loadFromNetwork().await()

            CoroutineScope(Dispatchers.Main).launch {
                newsList.value = newData
                isLoading.value = false
            }
        }
    }










}