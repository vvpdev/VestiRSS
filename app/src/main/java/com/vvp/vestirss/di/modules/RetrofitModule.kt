package com.vvp.vestirss.di.modules

import com.vvp.vestirss.repository.remote.RetrofitFactory
import dagger.Module
import dagger.Provides


@Module
class RetrofitModule {

    @Provides
    fun provideRetrofitFactory(): RetrofitFactory {
        return RetrofitFactory()
    }
}