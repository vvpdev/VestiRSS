package com.vvp.vestirss.repository.network

import android.util.Log
import android.util.Xml
import com.vvp.vestirss.App
import com.vvp.vestirss.repository.models.NewsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import javax.inject.Inject

class DataProvider {

    @Inject
    lateinit var retrofitFactory: RetrofitFactory

    init {
        App.diComponent!!.injectDataProvider(this)
    }


    // загрузка новостей
    suspend fun getNewsList(): Deferred<ArrayList<NewsModel>>{

        return CoroutineScope(Dispatchers.IO).async {

            // массив для возвращаемых данных
            val newsList: ArrayList<NewsModel> = ArrayList()

            try {

               val inputStream: InputStream = retrofitFactory.getRssService().getRssData().byteStream()

                val parser: XmlPullParser = Xml.newPullParser()

                parser.setInput(inputStream, null)

                //считывание значений с xml
                while (parser.eventType != XmlPullParser.END_DOCUMENT) {

                    if (parser.eventType == XmlPullParser.START_TAG && parser.name == "item") {

                        val newsModel = NewsModel()

                        while (parser.name != "title") {
                            parser.next()
                        }
                        newsModel.title = parser.nextText()

                        while (parser.name != "pubDate") {
                            parser.next()
                        }
                        newsModel.pubDate = parser.nextText()

                        while (parser.name != "category") {
                            parser.next()
                        }
                        newsModel.category = parser.nextText()

                        while (parser.name != "enclosure") {
                            parser.next()
                        }
                        newsModel.imageUrl = parser.getAttributeValue(0)

                        while (parser.name != "full-text") {
                            parser.next()
                        }
                        newsModel.fullText = parser.nextText()

                        newsList.add(newsModel)

                    } else {
                        parser.next()
                    }
                }


                val indexPlus: Int = newsList[0].pubDate!!.indexOf("+")
                val lastIndex: Int = newsList[0].pubDate!!.lastIndex

                // форматирование времени
                newsList.forEach {
                    val newData = it.pubDate!!.removeRange(indexPlus .. lastIndex)
                    it.pubDate = newData
                }
            }

            catch (e: Exception){
                Log.i("VestiRSS_Log", "network is not available")
            }

            return@async newsList
        }
    }














}