package com.vvp.vestirss.repository.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vvp.vestirss.repository.storage.tools.LastIndex
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import com.vvp.vestirss.repository.storage.models.NewsModel
import com.vvp.vestirss.repository.storage.tools.NewsQuantity


@Dao
interface MethodsDAO {


    // для новостей

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewsList(newsList: List<NewsModel>): LongArray

    @Query("DELETE FROM NewsModel")
    fun deleteAllNews()

    @Query("SELECT * FROM NewsModel where id = :id")
    fun getNewsById(id: Int): NewsModel

    @Query("SELECT id, title, pubDate, imageUrl, category FROM NewsModel")
    fun getAllMinNews(): List<MinNewsModel>

    @Query("SELECT id, title, pubDate, imageUrl, category FROM NewsModel where id = :id")
    fun getMinNewsById(id: Int): MinNewsModel

    @Query("SELECT id, title, pubDate, imageUrl, category FROM NewsModel where category = :category")
    fun getAllMinNews(category: String): List<MinNewsModel>



    // индекс последней новости
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLastIndex(lastIndex: LastIndex)

    @Query("SELECT * FROM LastIndex where id = :id")
    fun getLastIndexById(id: Int): LastIndex


    // количество сохраненных новостей в БД
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuantity(quantity: NewsQuantity)

    @Query("SELECT * FROM NewsQuantity where id = :id")
    fun getQuantityById(id: Int): NewsQuantity

}