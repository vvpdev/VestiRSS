package com.vvp.vestirss.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vvp.vestirss.R
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import com.vvp.vestirss.utils.NewsDiffUtils
import com.vvp.vestirss.utils.NewsListClass
import com.vvp.vestirss.utils.NumbersDiffUtils
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class AdapterNewsList(private val listener: ItemClick, private val buttonsListener: ButtonClick): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    // listener for news items
    interface ItemClick{
        fun onClick(view: View, news: MinNewsModel)
    }


    //listener for buttons
    interface ButtonClick{
        fun onNextCLick()
        fun onBackCLick()
    }


    // внутренний массив для новостей
    private var currentNewsList: LinkedList<MinNewsModel> = LinkedList()

    // внутренний массив для номеров страниц
    private var currentNumbersList: ArrayList<Int> = ArrayList()


    // обновление массива новостей
    fun updateNews(newsClass: NewsListClass){

        currentNumbersList.add(1)
        currentNumbersList.add(1)


        val newNumbersList: ArrayList<Int> = ArrayList()
        newsClass.currentNumber?.let { newNumbersList.add(it) }
        newsClass.lastNumber?.let { newNumbersList.add(it) }

        // если текущий массив не равен новому
        if (currentNewsList != newsClass.newsList){

            // сортировка по убыванию времени
            newsClass.newsList?.sortedBy { it.pubDate }

            val newsDiffUtil = newsClass.newsList?.let { NewsDiffUtils(oldList = currentNewsList, newsList = it) }

            currentNewsList.clear()

            newsClass.newsList?.forEach {
                currentNewsList.addFirst(it)
            }

            if (newNumbersList[0] != 0){
                currentNumbersList[0] = newNumbersList[0]
                currentNumbersList[1] = newNumbersList[1]
            }

            // обновление списка новостей
            newsDiffUtil?.let { DiffUtil.calculateDiff(it) }?.dispatchUpdatesTo(this)

            // обновление номеров страниц
            val numbersDiffUtils = NumbersDiffUtils(oldList = currentNumbersList, newList = newNumbersList)
            numbersDiffUtils.let { DiffUtil.calculateDiff(it) }.dispatchUpdatesTo(this)
        }
    }


    // очистка списка
    fun clearNewsList(){
        currentNewsList.clear()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType){

                R.layout.news_cell -> {val view = LayoutInflater.from(parent.context)
                                                .inflate(R.layout.news_cell, parent, false)
                                                NewsViewHolder(view) }

                R.layout.control_buttons ->  {val view = LayoutInflater.from(parent.context)
                                                .inflate(R.layout.control_buttons, parent, false)
                                                ButtonsViewHolder(view)}

            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }



    override fun getItemCount(): Int {
        return currentNewsList.count() + 1
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)){

                R.layout.news_cell -> (holder as NewsViewHolder).bindElements(news = currentNewsList[position], action = listener)

                R.layout.control_buttons ->  {

                    try {
                        (holder as ButtonsViewHolder).bindButtons(click = buttonsListener,
                                                                  currentNumber = currentNumbersList[0],
                                                                  lastNumber = currentNumbersList[1])
                    }
                    catch (e: Exception){
                        Log.i("VestiRSS_Log", "AdapterNewsList empty list")
                    }
                }
        }
    }



    // холдер для ячейки новости
    inner class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        // заголовок
        private var textTitleNewsCell: TextView = itemView.findViewById(R.id.textTitleCell)

        // время
        private var textDateNewsCell: TextView = itemView.findViewById(R.id.textDateCell)

        // изображение
        private var imageNewsCell: ImageView = itemView.findViewById(R.id.imageNewsCell)

        // карточка
        private var layoutCell: RelativeLayout = itemView.findViewById(R.id.layoutCell)


        fun bindElements(news: MinNewsModel, action: ItemClick): String{

            this.textTitleNewsCell.text = news.title

            this.textDateNewsCell.text = news.pubDate

            if (news.imageUrl != null){
                Glide
                    .with(itemView)
                    .load(news.imageUrl)
                    .into(this.imageNewsCell)
            }

            this.layoutCell.setOnClickListener{ action.onClick(view = itemView, news =  news)  }

            return "newsItem"
        }
    }


    // холдер для кнопок
    inner class ButtonsViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

        private val backButtons: ImageButton = itemView.findViewById(R.id.back_button)
        private val nextButtons: ImageButton = itemView.findViewById(R.id.next_button)

        private val textCurrentNumber: TextView = itemView.findViewById(R.id.textCurrentNumber)
        private val textLastNumber: TextView = itemView.findViewById(R.id.textLastNumber)

        // функция для кнопок
        fun bindButtons(click: ButtonClick, currentNumber: Int, lastNumber: Int): String{

            backButtons.setOnClickListener { click.onBackCLick() }
            nextButtons.setOnClickListener { click.onNextCLick() }


            if (currentNumber == lastNumber){
                nextButtons.isEnabled = false
                nextButtons.alpha = 0.4F
            } else {
                nextButtons.isEnabled = true
                nextButtons.alpha = 1F
            }


            if (currentNumber == 1){
                backButtons.isEnabled = false
                backButtons.alpha = 0.4F
            } else{
                backButtons.isEnabled = true
                backButtons.alpha = 1F
            }

            textCurrentNumber.text = currentNumber.toString()
            textLastNumber.text = lastNumber.toString()

            return "controlButtons"
        }
    }



    override fun getItemViewType(position: Int): Int {

        return when (position){

                currentNewsList.size -> R.layout.control_buttons

                else -> R.layout.news_cell
        }
    }
}