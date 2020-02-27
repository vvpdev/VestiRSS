package com.vvp.vestirss.repository.datebase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vvp.vestirss.repository.models.MinNewsModel
import com.vvp.vestirss.repository.models.NewsModel


@Dao
interface MethodsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewsList(newsList: List<NewsModel>)

    @Query("DELETE FROM NewsModel")
    fun deleteAllNews()

    @Query("SELECT * FROM NewsModel where title = :title")
    fun getNewsByTitle(title: String): NewsModel

    @Query("SELECT title, pubDate, category FROM NewsModel")
    fun getAllMinNews(): List<MinNewsModel>

    @Query("SELECT title, pubDate, category FROM NewsModel where category = :category")
    fun getAllMinNews(category: String): List<MinNewsModel>
}