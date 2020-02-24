package com.vvp.vestirss.di.modules

import com.vvp.vestirss.repository.network.retrofit.RetrofitFactory
import dagger.Module
import dagger.Provides


@Module
class RetrofitModule {

    @Provides
    fun provideRetrofitFactory(): RetrofitFactory{
        return RetrofitFactory()
    }
}