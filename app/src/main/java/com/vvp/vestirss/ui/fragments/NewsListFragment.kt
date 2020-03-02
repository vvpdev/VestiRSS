package com.vvp.vestirss.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vvp.vestirss.R
import com.vvp.vestirss.adapters.AdapterNewsList
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import com.vvp.vestirss.utils.NewsListStates
import com.vvp.vestirss.viewmodels.ListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsListFragment : Fragment(), AdapterNewsList.ItemClick {

    // viewModel для экрана списка новостей
    private lateinit var viewModel: ListViewModel

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
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)


        //setup recyclerView
        manager = LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        adapter = AdapterNewsList(this)
        recyclerViewNewsList.layoutManager = manager
        recyclerViewNewsList.adapter = adapter


        // наблюдение за заполненностью БД
        viewModel.isSavedNews.observe(viewLifecycleOwner, Observer {

            if (it == true){
                setHasOptionsMenu(true)
            } else {
                setHasOptionsMenu(false)
            }
        })


        // изменение текущего состояния
        viewModel.statesList.observe(viewLifecycleOwner, Observer {

            when (it) {

                // изначальное состояние, когда загрузка идет из БД
                is NewsListStates.LoadedFromDBState -> {

                    if (it.newsList.isNullOrEmpty()) {
                        textViewMessage.visibility = View.VISIBLE
                        textViewMessage.text = getText(R.string.empty_data_from_db)
                    } else {
                        textViewMessage.visibility = View.GONE
                        adapter.updateNews(newList = it.newsList)
                    }
                }

                // загрузка из сети, когда БД пустая
                is NewsListStates.InitLoadFromNetworkState -> {

                    if (it.newsList.isNullOrEmpty()) {
                        textViewMessage.visibility = View.VISIBLE
                        textViewMessage.text = getText(R.string.error_load_news_list)
                    } else {
                        recyclerViewNewsList.visibility = View.VISIBLE
                        textViewMessage.visibility = View.GONE
                        adapter.updateNews(newList = it.newsList)
                    }
                }

                // подгрузка новых новостей
                is NewsListStates.LoadNewDataState -> {
                    textViewMessage.visibility = View.GONE
                    adapter.updateNews(newList = it.newsList)
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
                    textViewMessage.visibility = View.VISIBLE
                    textViewMessage.text = getText(R.string.empty_data_from_db)
                }
            }
        })


        // показ прогресса
        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
           swipeLoadNews.isRefreshing = it })


        // swipe для загрузки новых данных
        swipeLoadNews.setOnRefreshListener { viewModel.separateLoad() }
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
    override fun onClick(view: View, news: MinNewsModel) {
        val newsBundle: Bundle = bundleOf("newsItem" to news.title)
        findNavController().navigate(R.id.action_to_newsDetailsFragment, newsBundle)
    }



    override fun onResume() {
        super.onResume()

        if (viewModel.statesList.value == null){
            viewModel.loadFromDB()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroy()
    }


    // отображение категорий сортировки
    private fun showSortScreen() {

        val categoryList = resources.getStringArray(R.array.title_category_list)

        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.text_category)
                .setItems(categoryList) { _, which ->
                    viewModel.loadNewsFromCategory(category = categoryList[which])
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
                viewModel.clearDB()
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
