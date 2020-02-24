package com.vvp.vestirss.repository.datebase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vvp.vestirss.repository.models.NewsModel


@Database(entities = [NewsModel::class], version = 1, exportSchema = false)
abstract class NewsDateBase: RoomDatabase() {

    abstract fun methodsDao(): MethodsDAO
}