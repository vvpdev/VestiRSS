package com.vvp.vestirss.views

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.vvp.vestirss.repository.NewsModel


@StateStrategyType(value = AddToEndStrategy::class)
interface NewsDetailsView: MvpView {

        fun getNewsData()

        fun showProgress(show: Boolean)

        fun showDataOnScreen(news: NewsModel)
}