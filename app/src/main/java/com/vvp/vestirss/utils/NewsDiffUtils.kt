package com.vvp.vestirss.utils

import androidx.recyclerview.widget.DiffUtil
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import java.util.*
import kotlin.collections.ArrayList

class NewsDiffUtils (private val oldList: LinkedList<MinNewsModel>, private val newsList: ArrayList<MinNewsModel>): DiffUtil.Callback() {


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemPosition == newItemPosition
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newsList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newsList[newItemPosition]
    }


}