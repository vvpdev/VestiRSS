package com.vvp.vestirss.views

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.vvp.vestirss.repository.NewsModel
import java.util.*


@StateStrategyType(value = AddToEndStrategy::class)
interface NewsListView: MvpView {

    //SkipStrategy - для отсутствия повторов команды (одиночное выполнение)

    @StateStrategyType(value = SkipStrategy::class)
    fun showProgress(show: Boolean)

    fun showNewsList(newsLIst: LinkedList<NewsModel>)

    @StateStrategyType(value = SkipStrategy::class)
    fun showMessage(message: Int)

    @StateStrategyType(value = SkipStrategy::class)
    fun showMessage(message: String)

    fun showSortScreen()

    fun showTextViewMessage(message: Int)
    fun showTextViewMessage(message: String)

    fun showDialogForClearDB()

    fun showButtonToolbar(show: Boolean)
}