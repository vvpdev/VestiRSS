package com.vvp.vestirss.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vvp.vestirss.R
import kotlinx.android.synthetic.main.activity_main.*


class NewsDetailsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.toolbar.title = getString(R.string.title_news_list_screen)
    }


    //___________________________
    //View implementation


//    override fun getNewsData() {
//
//        val selectedNews: NewsModel? = arguments!!.getParcelable("selectedNewsItem")
//        presenter.takeData(selectedNews!!)
//    }
//
//
//    override fun showProgress(show: Boolean) {
//
//        if (show) {
//
//            progressLoadNewsDetails.visibility = View.VISIBLE
//            textTitleFullNews.visibility = View.GONE
//            textFullTextNews.visibility = View.GONE
//            imageNews.visibility = View.GONE
//        } else {
//
//            progressLoadNewsDetails.visibility = View.GONE
//            textTitleFullNews.visibility = View.VISIBLE
//            textFullTextNews.visibility = View.VISIBLE
//            imageNews.visibility = View.VISIBLE
//        }
//    }
//
//
//    override fun showDataOnScreen(news: NewsModel) {
//
//        textTitleFullNews.text = news.title
//        textFullTextNews.text = news.fullText
//
//        loadImage(news.imageUrl)
//    }
//
//
//    private fun loadImage(url: String?) {
//        if (url.isNullOrEmpty()) {
//            imageNews.visibility = View.GONE
//        } else {
//            imageNews.visibility = View.VISIBLE
//            Glide
//                .with(this)
//                .load(url)
//                .centerCrop()
//                .placeholder(R.drawable.placeholder)      // если изображение не загружено
//                .into(imageNews)
//        }
//    }

}
