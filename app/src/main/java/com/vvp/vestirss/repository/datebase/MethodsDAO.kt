package com.vvp.vestirss.repository.datebase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vvp.vestirss.repository.models.NewsModel


@Dao
interface MethodsDAO {

    @Query("SELECT * FROM NewsModel")
    fun getAllNews(): List<NewsModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewsList(newsList: List<NewsModel>)

    @Query("DELETE FROM NewsModel")
    fun deleteAllNews()

    @Query("SELECT * FROM NewsModel where category = :category")
    fun getNewsSelectedCategory(category: String): List<NewsModel>

    @Query("SELECT * FROM NewsModel where title = :title")
    fun getNewsByTitle(title: String): NewsModel
}