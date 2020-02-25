package com.vvp.vestirss.utils

import com.vvp.vestirss.repository.models.NewsModel

sealed class NewsListStates {


    // загрузка
    object LoadingState: NewsListStates()

    // показ сохраненных новостей
    class LoadedFromDBState(val newsList: ArrayList<NewsModel>): NewsListStates()

    // загрузка из сети, когда БД пустая
    class InitLoadFromNetworkState(val newsList: ArrayList<NewsModel>): NewsListStates()

    // подгрузка новых новостей
    class LoadNewDataState(val newsList: ArrayList<NewsModel>, val isNew: Boolean): NewsListStates()

    // показ выбранной категории
    class SortState(val newsList: ArrayList<NewsModel>): NewsListStates()


    // состояние, когда БД очищена
    object EmptyDBState: NewsListStates()

}