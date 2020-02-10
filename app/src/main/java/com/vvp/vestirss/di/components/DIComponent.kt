package com.vvp.vestirss.di.components

import com.vvp.vestirss.di.modules.*
import com.vvp.vestirss.presenters.NewsDetailsPresenter
import com.vvp.vestirss.presenters.NewsListPresenter
import com.vvp.vestirss.repository.network.retrofit.DataProvider
import dagger.Component


@Component(modules = [DataProviderModule::class, NewsDateBaseModul::class, RetrofitModul::class, ConverterModule::class])
interface DIComponent {

    // инжектирование всех переменных

    fun injectNewsListPresenter (presenter: NewsListPresenter)

    fun injectNewsDetailsPresenter (presenter: NewsDetailsPresenter)

    fun injectDataProvider(provider: DataProvider)

}