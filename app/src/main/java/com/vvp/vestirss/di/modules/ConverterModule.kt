package com.vvp.vestirss.di.modules

import com.vvp.vestirss.converters.DataConverter
import dagger.Module
import dagger.Provides

@Module
class ConverterModule {

    @Provides
    fun provideConverter(): DataConverter {
        return DataConverter()
    }
}