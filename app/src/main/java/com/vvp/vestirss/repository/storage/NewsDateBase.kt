package com.vvp.vestirss.repository.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vvp.vestirss.repository.storage.models.NewsModel


@Database(entities = [NewsModel::class], version = 1, exportSchema = false)
abstract class NewsDateBase: RoomDatabase() {

    abstract fun methodsDao(): MethodsDAO
}