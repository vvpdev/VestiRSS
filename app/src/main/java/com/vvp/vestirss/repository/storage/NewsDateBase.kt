package com.vvp.vestirss.repository.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vvp.vestirss.repository.storage.models.LastIndex
import com.vvp.vestirss.repository.storage.models.NewsModel


@Database(entities = [NewsModel::class, LastIndex::class], version = 1, exportSchema = false)
abstract class NewsDateBase: RoomDatabase() {

    abstract fun methodsDao(): MethodsDAO
}