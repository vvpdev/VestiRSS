package com.vvp.vestirss.repository.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vvp.vestirss.repository.storage.models.LastIndex
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import com.vvp.vestirss.repository.storage.models.NewsModel


@Dao
interface MethodsDAO {


    // для новостей

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewsList(newsList: List<NewsModel>): LongArray

    @Query("DELETE FROM NewsModel")
    fun deleteAllNews()

    @Query("SELECT * FROM NewsModel where id = :id")
    fun getNewsById(id: Int): NewsModel

    @Query("SELECT id, title, pubDate, category FROM NewsModel")
    fun getAllMinNews(): List<MinNewsModel>

    @Query("SELECT id, title, pubDate, category FROM NewsModel where id = :id")
    fun getMinNewsById(id: Int): MinNewsModel

    @Query("SELECT id, title, pubDate, category FROM NewsModel where category = :category")
    fun getAllMinNews(category: String): List<MinNewsModel>



    // сохранение позиции

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLastIndex(lastIndex: LastIndex)

    @Query("SELECT * FROM LastIndex where id = :id")
    fun getLastIndexById(id: Int): LastIndex


}