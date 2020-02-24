package com.vvp.vestirss.di.components

import com.vvp.vestirss.di.modules.*

import com.vvp.vestirss.repository.RepositoryClass
import com.vvp.vestirss.repository.network.retrofit.DataProvider
import com.vvp.vestirss.viewmodels.NewsListViewModel
import dagger.Component


@Component(modules = [DataProviderModule::class,
                      NewsDateBaseModule::class,
                      RetrofitModule::class,
                      ConverterModule::class,
                      RepositoryModule::class])

interface DIComponent {

    // инжектирование всех переменных
    fun injectNewsListViewModel(viewModel: NewsListViewModel)

    fun injectDataProvider(provider: DataProvider)

    fun injectRepositoryClass(repositoryClass: RepositoryClass)

}