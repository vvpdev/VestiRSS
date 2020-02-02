package com.vvp.vestirss.di.modules

import android.content.Context
import androidx.room.Room
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.datebase.MethodsDAO
import com.vvp.vestirss.repository.datebase.NewsDateBase
import dagger.Module
import dagger.Provides


@Module
class NewsDateBaseModul {

    @Provides
    fun provideContext(): Context {
        return App.context!!
    }


    @Provides
    fun provideDateBase(): NewsDateBase {
        return Room.databaseBuilder(provideContext(), NewsDateBase::class.java, "newsDate").build()
    }


    @Provides
    fun provideDao(newsDateBase: NewsDateBase): MethodsDAO {
        return newsDateBase.methodsDao()
    }

}