package com.vvp.vestirss.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.RepositoryClass
import com.vvp.vestirss.repository.storage.models.NewsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailsViewModel: ViewModel() {

    @Inject
    lateinit var repository: RepositoryClass

    // liveData для новости
    val newsInstance: MutableLiveData<NewsModel> =  MutableLiveData()

    init {
        App.diComponent?.injectDetailsNewsViewModel(this)

    }


    fun getNews(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            newsInstance.postValue(repository.getNewsById(id = id))
        }
    }


    fun onDestroy(){
        newsInstance.value = null
    }
}