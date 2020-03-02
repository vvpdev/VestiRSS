package com.vvp.vestirss.utils

import com.vvp.vestirss.repository.storage.models.MinNewsModel

sealed class NewsListStates {


    // показ сохраненных новостей
    class LoadedFromDBState(val newsList: ArrayList<MinNewsModel>): NewsListStates()

    // загрузка из сети, когда БД пустая
    class InitLoadFromNetworkState(val newsList: ArrayList<MinNewsModel>): NewsListStates()

    // подгрузка новых новостей
    class LoadNewDataState(val newsList: ArrayList<MinNewsModel>): NewsListStates()

    // показ выбранной категории
    class SortState(val newsList: ArrayList<MinNewsModel>): NewsListStates()

    // состояние, когда БД очищена
    object EmptyDBState: NewsListStates()

}