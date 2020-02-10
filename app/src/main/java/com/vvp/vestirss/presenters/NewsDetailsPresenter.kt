package com.vvp.vestirss.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.vvp.vestirss.repository.NewsModel
import com.vvp.vestirss.views.NewsDetailsView


@InjectViewState
class NewsDetailsPresenter: MvpPresenter<NewsDetailsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.showProgress(true)
        viewState.getNewsData()
    }

    fun takeData(news: NewsModel){

        viewState.showDataOnScreen(news)
        viewState.showProgress(false)
    }
}