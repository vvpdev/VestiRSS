package com.vvp.vestirss.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vvp.vestirss.R
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import com.vvp.vestirss.utils.NewsDiffUtils
import java.util.*

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



    // внутренний массив для данных
    private var currentNewsList: LinkedList<MinNewsModel> = LinkedList()


    // обновление массива новостей
    fun updateNews(newList: ArrayList<MinNewsModel>){

        // если текущий массив не равен новому
        if (currentNewsList != newList){

            // сортировка по убыванию времени
            newList.sortedBy { it.pubDate }

            val diffUtil = NewsDiffUtils(oldList = currentNewsList, newsList = newList)
            val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(diffUtil)

            currentNewsList.clear()

            newList.forEach {
                currentNewsList.addFirst(it)
            }

            diffResult.dispatchUpdatesTo(this)
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

                R.layout.next_back_buttons ->  {val view = LayoutInflater.from(parent.context)
                                                .inflate(R.layout.next_back_buttons, parent, false)
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

                R.layout.next_back_buttons ->  ( holder as ButtonsViewHolder).bindButtons(click = buttonsListener)
        }
    }



    // холдер для ячейки новости
    inner class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        // заголовок
        private var textTitleNewsCell: TextView = itemView.findViewById(R.id.textTitleNewsCell)

        // время
        private var textDateNewsCell: TextView = itemView.findViewById(R.id.textDateNewsCell)

        fun bindElements(news: MinNewsModel, action: ItemClick){

            this.textTitleNewsCell.text = news.title

            this.textDateNewsCell.text = news.pubDate

            this.textTitleNewsCell.setOnClickListener { action.onClick(view = itemView, news =  news) }
        }
    }


    // холдер для кнопок
    inner class ButtonsViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

        private val backButtons: Button = itemView.findViewById(R.id.back_button)
        private val nextButtons: Button = itemView.findViewById(R.id.next_button)

        // функция для кнопок
        fun bindButtons(click: ButtonClick){

            backButtons.setOnClickListener { click.onBackCLick() }
            nextButtons.setOnClickListener { click.onNextCLick() }
        }
    }



    override fun getItemViewType(position: Int): Int {

        return when (position){

                currentNewsList.size -> R.layout.next_back_buttons

                else -> R.layout.news_cell
        }
    }
}