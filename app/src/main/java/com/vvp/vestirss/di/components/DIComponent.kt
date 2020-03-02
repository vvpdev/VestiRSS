package com.vvp.vestirss.di.components

import com.vvp.vestirss.di.modules.*

import com.vvp.vestirss.repository.RepositoryClass
import com.vvp.vestirss.repository.remote.DataProvider
import com.vvp.vestirss.viewmodels.DetailsViewModel
import com.vvp.vestirss.viewmodels.ListViewModel
import dagger.Component
import javax.inject.Singleton


@Component(modules = [DataProviderModule::class,
                      DateBaseModule::class,
                      RetrofitModule::class,
                      RepositoryModule::class])

@Singleton
interface DIComponent {


    fun injectNewsListViewModel(viewModel: ListViewModel)

    fun injectDetailsNewsViewModel(viewModel: DetailsViewModel)

    fun injectDataProvider(provider: DataProvider)

    fun injectRepositoryClass(repositoryClass: RepositoryClass)

}