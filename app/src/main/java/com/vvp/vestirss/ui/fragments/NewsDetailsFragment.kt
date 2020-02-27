package com.vvp.vestirss.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.vvp.vestirss.R
import com.vvp.vestirss.viewmodels.DetailsNewsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_news_details.*


class NewsDetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsNewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // привязка viewModel к фрагменту
        viewModel = ViewModelProvider(this).get(DetailsNewsViewModel::class.java)

        activity!!.toolbar.title = getString(R.string.title_news_list_screen)


        getNewsData()


        viewModel.newsInstance.observe(viewLifecycleOwner, Observer {

            imageNews.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(it.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder)      // если изображение не загружено
                .into(imageNews)

            textTitleFullNews.text = it.title
            textFullTextNews.text = it.fullText
        })
    }


    private fun getNewsData() {

        val newsTitle: String = arguments?.getString("newsItem") ?: ""
        viewModel.getNews(title = newsTitle)
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
}
