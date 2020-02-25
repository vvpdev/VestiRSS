package com.vvp.vestirss.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vvp.vestirss.R
import com.vvp.vestirss.adapters.AdapterNewsList
import com.vvp.vestirss.repository.models.NewsModel
import com.vvp.vestirss.utils.NewsListStates
import com.vvp.vestirss.viewmodels.NewsListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsListFragment : Fragment(), AdapterNewsList.onClickListener {

    // viewModel для экрана списка новостей
    private lateinit var newsViewModel: NewsListViewModel

    // for recyclerView
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: AdapterNewsList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // заголовк в тулбаре
        activity!!.toolbar.title = getString(R.string.title_news_list_screen)

        // привязка viewModel к фрагменту
        newsViewModel = ViewModelProvider(this).get(NewsListViewModel::class.java)


        //setup recyclerView
        manager = LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        adapter = AdapterNewsList(this)
        recyclerViewNewsList.layoutManager = manager
        recyclerViewNewsList.adapter = adapter


        // наблюдение за заполненностью БД
        newsViewModel.isSavedNews.observe(viewLifecycleOwner, Observer {

            if (it == true){
                setHasOptionsMenu(true)     // toolbar menu на этом фрагменте
                Log.i("VestiRSS_Log", "NewsListFragment observe for isSavedNews = $it")
            } else {
                setHasOptionsMenu(false)
                Log.i("VestiRSS_Log", "NewsListFragment observe for isSavedNews = $it")
            }
        })


        // наблюдение за текущим состоянием
        newsViewModel.newsListState.observe(viewLifecycleOwner, Observer {

            when (it) {

                // состояние загрузки
                is NewsListStates.LoadingState -> {
                    swipeLoadNews.isRefreshing = true
                    textViewMessage.visibility = View.GONE
                }


                // изначальный state, когда загрузка идет из БД
                is NewsListStates.LoadedFromDBState -> {
                    swipeLoadNews.isRefreshing = false

                    if (it.newsList.isNullOrEmpty()) {
                        textViewMessage.visibility = View.VISIBLE
                        textViewMessage.text = getText(R.string.empty_data_from_db)
                    } else {
                        textViewMessage.visibility = View.GONE
                        adapter.updateNews(newList = it.newsList)
                    }
                }

                // изначальная загрузка из сети, когда БД пустая
                is NewsListStates.InitLoadFromNetworkState -> {
                    swipeLoadNews.isRefreshing = false

                    if (it.newsList.isNullOrEmpty()) {
                        textViewMessage.visibility = View.VISIBLE
                        textViewMessage.text = getText(R.string.error_load_news_list)
                    } else {
                        textViewMessage.visibility = View.GONE
                        adapter.updateNews(newList = it.newsList)
                    }
                }

                // подгрузка новых новостей
                is NewsListStates.LoadNewDataState -> {
                    swipeLoadNews.isRefreshing = false
                    textViewMessage.visibility = View.GONE

                    if (it.isNew) {
                        Toast.makeText(
                            activity,
                            getText(R.string.new_data_uploaded),
                            Toast.LENGTH_SHORT
                        ).show()
                        adapter.updateNews(newList = it.newsList)
                    } else {
                        Toast.makeText(activity, getText(R.string.no_new_data), Toast.LENGTH_SHORT)
                            .show()
                        adapter.updateNews(newList = it.newsList)
                    }
                }

                // сортировка по категориям
                is NewsListStates.SortState -> {

                    if (it.newsList.isNullOrEmpty()) {
                        recyclerViewNewsList.visibility = View.GONE
                        textViewMessage.visibility = View.VISIBLE
                        textViewMessage.text = getText(R.string.empty_data_from_category)
                    } else {
                        recyclerViewNewsList.visibility = View.VISIBLE
                        textViewMessage.visibility = View.GONE
                        adapter.updateNews(newList = it.newsList)
                    }
                }

                is NewsListStates.EmptyDBState -> {
                    swipeLoadNews.isRefreshing = false
                    textViewMessage.visibility = View.VISIBLE
                    textViewMessage.text = getText(R.string.empty_data_from_db)
                }
            }
        })


        // swipe для загрузки новых данных
        swipeLoadNews.setOnRefreshListener {  newsViewModel.separateLoad()  }
    }


    // toolbar menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.news_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.sort_item -> {  showSortScreen()  }                        // показ AlertDialog с категориями для сортировки
            R.id.clear_data_base_item -> {  showDialogForClearDB()  }       // показ AlertDialog с вопросом о удалении
        }
        return super.onOptionsItemSelected(item)
    }


    // переход к фрагменту деталировки и передача выбранной новости
    override fun onClick(view: View, news: NewsModel) {
        Toast.makeText(activity, "выбранный элемент № ${news.id}", Toast.LENGTH_SHORT).show()

//        val newsBundle: Bundle = bundleOf("selectedNewsItem" to news)
//        findNavController().navigate(R.id.action_to_newsDetailsFragment, newsBundle)
    }



    override fun onResume() {
        super.onResume()
        newsViewModel.loadFromDB()
    }



    // отображение категорий сортировки
    private fun showSortScreen() {

        val categoryList = resources.getStringArray(R.array.title_category_list)

        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.text_category)
                .setItems(categoryList) { _, which ->
                    newsViewModel.loadNewsFromCategory(category = categoryList[which])
                }
                .create()
                .show()
        }
    }

    // запрос на удаление
    private fun showDialogForClearDB() {

        AlertDialog.Builder(activity!!)
            .setTitle(R.string.question_delete_all_from_db)

            .setPositiveButton(R.string.answer_yes) { _, _ ->
                newsViewModel.clearDB()
                adapter.clearNewsList()

                // очистить кэш изображений
                CoroutineScope(Dispatchers.IO).launch {
                    Glide.get(activity!!).clearDiskCache()
                }
            }
            .setNegativeButton(R.string.answer_no) { dialog, _ ->
                dialog.cancel()
            }
            .setCancelable(true)
            .create()
            .show()
    }
}
