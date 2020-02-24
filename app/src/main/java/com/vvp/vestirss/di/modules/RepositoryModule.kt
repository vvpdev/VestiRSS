package com.vvp.vestirss.di.modules

import com.vvp.vestirss.repository.RepositoryClass
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideRepository(): RepositoryClass{
        return RepositoryClass()
    }
}