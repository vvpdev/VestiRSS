package com.vvp.vestirss.repository.datebase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vvp.vestirss.repository.NewsModel


@Dao
interface MethodsDAO {

    @Query("SELECT * FROM NewsModel")
    fun getAllNews(): List<NewsModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewsList(newsList: List<NewsModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: NewsModel)

    @Query("DELETE FROM NewsModel")
    fun deleteAllNews()

}