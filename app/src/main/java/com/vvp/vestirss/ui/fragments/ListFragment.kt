package com.vvp.vestirss.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vvp.vestirss.R
import com.vvp.vestirss.adapters.AdapterNewsList
import com.vvp.vestirss.repository.storage.models.MinNewsModel
import com.vvp.vestirss.viewmodels.ListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.control_buttons.*
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListFragment : Fragment(), AdapterNewsList.ItemClick, AdapterNewsList.ButtonClick {

    // viewModel для экрана списка новостей
    private lateinit var viewModel: ListViewModel

    // for recyclerView

    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var adapter: AdapterNewsList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.toolbar.title = getString(R.string.title_news_list_screen)

        // привязка viewModel к фрагменту
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)



       adapter = AdapterNewsList(listener = this, buttonsListener = this)

       // orientation
       val configuration: Configuration = resources.configuration

        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }
        else {
            manager = GridLayoutManager(activity, 2)
        }



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


        // изменение текущего состояния списка новостей
        viewModel.newsList.observe(viewLifecycleOwner, Observer {

                if (it != null && !it.newsList.isNullOrEmpty()){
                    recyclerViewNewsList.visibility = View.VISIBLE
                    adapter.updateNews(newsClass = it)
                    textViewMessage.visibility = View.GONE

                } else {
                    recyclerViewNewsList.visibility = View.GONE
                    textViewMessage.visibility = View.VISIBLE
                    textViewMessage.text = getText(R.string.empty_data_from_db)
                }
        })


        // показ прогресса
        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
           swipeLoadNews.isRefreshing = it })


        // swipe для загрузки новых данных
        swipeLoadNews.setOnRefreshListener { viewModel.separateLoad() }


        // show toast
        viewModel.messageStorage.observe(viewLifecycleOwner, Observer {
            if (it != null){
                Toast.makeText(activity, getText(it), Toast.LENGTH_SHORT).show()
            } })
    }


    // toolbar menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.news_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            //R.id.sort_item -> {  showSortScreen()  }                        // показ AlertDialog с категориями для сортировки
            R.id.clear_data_base_item -> {  showDialogForClearDB()  }       // показ AlertDialog с вопросом о удалении
        }
        return super.onOptionsItemSelected(item)
    }



    // переход к фрагменту деталировки и передача id выбранной новости
    override fun onClick(view: View, news: MinNewsModel) {
        val newsBundle: Bundle = bundleOf("newsId" to news.id)
        findNavController().navigate(R.id.action_to_newsDetailsFragment, newsBundle)
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
                   // viewModel.loadNewsFromCategory(category = categoryList[which])
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
                recyclerViewNewsList.visibility = View.GONE
                viewModel.clearDB()
                adapter.clearNewsList()

                // очистить кэш изображений
                CoroutineScope(Dispatchers.IO).launch {
                    Glide.get(activity!!).clearDiskCache()
                }

                textViewMessage.visibility = View.VISIBLE
                textViewMessage.text = getText(R.string.empty_data_from_db)

            }
            .setNegativeButton(R.string.answer_no) { dialog, _ ->
                dialog.cancel()
            }
            .setCancelable(true)
            .create()
            .show()
    }


    override fun onNextCLick() {
        viewModel.loadNextPage(currentIndex = (textCurrentNumber.text.toString()).toInt(),
                               lastIndex = (textLastNumber.text.toString()).toInt())
    }


    override fun onBackCLick() {
        viewModel.loadBackPage(currentIndex = (textCurrentNumber.text.toString()).toInt(),
                               lastIndex = (textLastNumber.text.toString()).toInt())
    }
}
