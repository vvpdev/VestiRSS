package com.vvp.vestirss.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vvp.vestirss.R
import com.vvp.vestirss.repository.models.MinNewsModel
import com.vvp.vestirss.utils.NewsDiffUtils
import java.util.*

class AdapterNewsList(private val listener: onClickListener): RecyclerView.Adapter<AdapterNewsList.ViewHolder>() {


    // listener
    interface onClickListener{
        fun onClick(view: View, news: MinNewsModel)
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(itemView = LayoutInflater.from(parent.context).inflate(R.layout.news_cell, parent, false))
    }

    override fun getItemCount(): Int {
        return currentNewsList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindElements(news = this.currentNewsList[position], action = listener)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        // заголовок
        private var textTitleNewsCell: TextView = itemView.findViewById(R.id.textTitleNewsCell)

        // время
        private var textDateNewsCell: TextView = itemView.findViewById(R.id.textDateNewsCell)

        fun bindElements(news: MinNewsModel, action: onClickListener){

            this.textTitleNewsCell.text = news.title

            this.textDateNewsCell.text = news.pubDate

            this.textTitleNewsCell.setOnClickListener{  action.onClick(view = itemView, news =  news) }
        }
    }
}