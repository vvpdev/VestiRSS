package com.vvp.vestirss.di.modules

import com.vvp.vestirss.repository.remote.RetrofitFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RetrofitModule {


    @Singleton
    @Provides
    fun provideRetrofitFactory(): RetrofitFactory {
        return RetrofitFactory()
    }
}