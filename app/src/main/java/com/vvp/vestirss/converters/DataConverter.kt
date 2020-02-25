package com.vvp.vestirss.converters

import com.vvp.vestirss.repository.models.NewsModel
import com.vvp.vestirss.repository.network.xml_models.Item

class DataConverter {

    // конвертирование xml модели в класс-модель
    fun convert(responseItems: List<Item>): ArrayList<NewsModel> {

        val newsList: ArrayList<NewsModel> = ArrayList()

        responseItems.forEach {
            newsList.add(

                NewsModel(
                    title = it.title!!,
                    pubDate = it.pubDate!!,
                    category = it.category!!,
                    imageUrl = it.enclosure!![0].url!!,
                    fullText = it.yandexFullText!!,
                    id = 0
                )
            )
        }

        val indexPlus: Int = newsList[0].pubDate.indexOf("+")
        val lastIndex: Int = newsList[0].pubDate.lastIndex

        newsList.forEach {

            val newData = it.pubDate.removeRange(indexPlus .. lastIndex)

            it.pubDate = newData
        }

        return newsList
    }
}