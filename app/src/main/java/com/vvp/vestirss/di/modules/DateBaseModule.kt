package com.vvp.vestirss.di.modules

import android.content.Context
import androidx.room.Room
import com.vvp.vestirss.repository.storage.MethodsDAO
import com.vvp.vestirss.repository.storage.NewsDateBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton



@Module
class DateBaseModule (val context: Context) {


    @Singleton
    @Provides
    fun provideDateBase(): NewsDateBase {
        return Room.databaseBuilder(context, NewsDateBase::class.java, "newsDate").build()
    }


    @Singleton
    @Provides
    fun provideDao(newsDateBase: NewsDateBase): MethodsDAO {
        return newsDateBase.methodsDao()
    }

}