package com.vvp.vestirss.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vvp.vestirss.R
import com.vvp.vestirss.adapters.AdapterNewsList
import com.vvp.vestirss.repository.models.NewsModel
import com.vvp.vestirss.viewmodels.NewsListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_news_list.*

class NewsListFragment : Fragment(), AdapterNewsList.onClickListener {

    // viewModel для экрана списка новостей
    private lateinit var newsViewModel: NewsListViewModel

    // for recyclerView
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: AdapterNewsList

    // для кнопок в тулбаре
    private lateinit var buttonSortItem: MenuItem
    private lateinit var buttonClearData: MenuItem


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


        // toolbar menu на этом фрагменте
        setHasOptionsMenu(true)


        //setup recyclerView
        manager = LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        adapter = AdapterNewsList(this)
        recyclerViewNewsList.layoutManager = manager
        recyclerViewNewsList.adapter = adapter


        // массив новостей
        newsViewModel.newsList.observe(viewLifecycleOwner, Observer {

            if (it.isNullOrEmpty()){
                textViewMessage.text = getText(R.string.empty_data_from_db)
            } else{
                adapter.updateNews(newList = it) }
            })


        // переменная загрузки
        newsViewModel.isLoading.observe(viewLifecycleOwner, Observer { swipeLoadNews.isRefreshing = it })


        // swipe для загрузки новых данных
        swipeLoadNews.setOnRefreshListener {  newsViewModel.updateNewsList()  }
    }



    // toolbar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.news_list_menu, menu)

        // инициализируем кнопки
        buttonSortItem = menu.findItem(R.id.sort_item)
        buttonClearData = menu.findItem(R.id.clear_data_base_item)

        super.onCreateOptionsMenu(menu, inflater)

//        // только при первом открытии программы
//        if (presenter.newsList.isNullOrEmpty()){
//            showButtonToolbar(false)
//        }
//        else{
//            showButtonToolbar(true)
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.sort_item -> {    }                        // Dialog с категориями для сортировки

            R.id.clear_data_base_item -> {    }       // Dialog с вопросом о удалении
        }
        return super.onOptionsItemSelected(item)
    }



    //______________________________
    //view implementation

    // управление показом прогресса
//    override fun showProgress(show: Boolean) {
//
//        swipeAction.isRefreshing = show
//        recyclerViewNewsList.isEnabled = show
//    }
//
//
//    // отображение списка новостей
//    override fun showNews(newsList: ArrayList<NewsModel>, addNews: Boolean) {
//
//        textViewMessage.visibility = View.GONE
//
//        // добавление новых новостей
//        if (addNews){
//            adapter.addNews(newsList)
//        }
//        else{
//            // обновление списка новостей
//            adapter.updateNews(newsList)
//        }
//    }
//
//
//    override fun showMessage(message: Int) {
//        Toast.makeText(activity, getText(message), Toast.LENGTH_SHORT).show()
//    }
//
//    override fun showMessage(message: String) {
//        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
//    }
//
//
    // переход к фрагменту деталировки и передача выбранной новости
    override fun onClick(view: View, news: NewsModel) {

        val newsBundle: Bundle = bundleOf("selectedNewsItem" to news)
        findNavController().navigate(R.id.action_to_newsDetailsFragment, newsBundle)
    }
//
//
//    // отображение категорий сортировки
//    override fun showSortScreen() {
//
//        val categoryList = resources.getStringArray(R.array.title_category_list)
//
//        AlertDialog.Builder(activity)
//            .setTitle(R.string.text_category)
//            .setItems(categoryList) { _, which ->
//
//                // передаем презентеру выбранную категорию
//                presenter.sortingNewsList(categoryList[which], categoryList[0])
//            }
//            .create()
//            .show()
//    }
//
//
//    // показ сообщений в textView на главном экране
//    override fun showTextViewMessage(message: Int) {
//
//        textViewMessage.visibility = View.VISIBLE
//        textViewMessage.text = getString(message)
//    }
//
//    override fun showTextViewMessage(message: String) {
//        textViewMessage.visibility = View.VISIBLE
//        textViewMessage.text = message
//    }
//
//
//    override fun showDialogForClearDB() {
//
//        AlertDialog.Builder(activity!!)
//            .setTitle(R.string.question_delete_all_from_db)
//
//            .setPositiveButton(R.string.answer_yes) { _, _ ->
//                presenter.clearDateBase()
//
//                // очистить кэш изображений
//                CoroutineScope(Dispatchers.IO).launch {
//                    Glide.get(activity!!).clearDiskCache()
//                }
//            }
//            .setNegativeButton(R.string.answer_no) { dialog, _ ->
//                dialog.cancel()
//            }
//            .setCancelable(true)
//            .create()
//            .show()
//    }
//
//
//    // скрытие/отображение кнопок в тулбаре
//    override fun showButtonToolbar(show: Boolean) {
//        buttonSortItem.isVisible = show
//        buttonClearData.isVisible = show
//    }
//
//
//    //save scroll position
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putParcelable("recState", manager.onSaveInstanceState())
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//
//        if (savedInstanceState != null){
//            manager.onRestoreInstanceState(savedInstanceState.getParcelable("recState"))
//        }
//    }
}
