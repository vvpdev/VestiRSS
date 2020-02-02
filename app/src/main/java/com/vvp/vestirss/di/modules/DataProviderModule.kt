package com.vvp.vestirss.di.modules

import com.vvp.vestirss.repository.network.retrofit.DataProvider
import dagger.Module
import dagger.Provides


@Module
class DataProviderModule {

    @Provides
    fun provideDataProvider(): DataProvider{
        return DataProvider()
    }
}