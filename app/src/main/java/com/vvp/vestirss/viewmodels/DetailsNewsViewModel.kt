package com.vvp.vestirss.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.RepositoryClass
import com.vvp.vestirss.repository.models.NewsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailsNewsViewModel: ViewModel() {

    @Inject
    lateinit var repository: RepositoryClass

    private var loadJob: Job? = null

    init {
        App.diComponent?.injectDetailsNewsViewModel(this)
    }

    // liveData для новости
    val newsInstance: MutableLiveData<NewsModel> =  MutableLiveData()


    fun getNews(title: String){
        loadJob = CoroutineScope(Dispatchers.IO).launch {
            newsInstance.postValue(repository.getNewsByTitle(title = title).await())
        }
    }


    fun onDestroy(){
        loadJob?.cancel()
        newsInstance.value = null
    }
}