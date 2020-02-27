package com.vvp.vestirss.di.components

import com.vvp.vestirss.di.modules.*

import com.vvp.vestirss.repository.RepositoryClass
import com.vvp.vestirss.repository.network.DataProvider
import com.vvp.vestirss.viewmodels.DetailsNewsViewModel
import com.vvp.vestirss.viewmodels.NewsListViewModel
import dagger.Component


@Component(modules = [DataProviderModule::class,
                      NewsDateBaseModule::class,
                      RetrofitModule::class,
                      RepositoryModule::class])

interface DIComponent {


    fun injectNewsListViewModel(viewModel: NewsListViewModel)

    fun injectDetailsNewsViewModel(viewModel: DetailsNewsViewModel)

    fun injectDataProvider(provider: DataProvider)

    fun injectRepositoryClass(repositoryClass: RepositoryClass)

}