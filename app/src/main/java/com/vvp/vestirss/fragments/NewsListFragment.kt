package com.vvp.vestirss.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.vvp.vestirss.R
import com.vvp.vestirss.adapters.AdapterNewsList
import com.vvp.vestirss.presenters.NewsListPresenter
import com.vvp.vestirss.repository.NewsModel
import com.vvp.vestirss.views.NewsListView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class NewsListFragment : MvpAppCompatFragment(), NewsListView, AdapterNewsList.onClickListener {

    @InjectPresenter
    lateinit var presenter: NewsListPresenter

    private lateinit var adapter: AdapterNewsList
    private lateinit var manager: LinearLayoutManager

    // для кнопок в тулбаре
    private lateinit var button_sort_item: MenuItem
    private lateinit var button_clear_data: MenuItem



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.toolbar.title = getString(R.string.title_news_list_screen)

        setHasOptionsMenu(true)

        // swipe для загрузки новых данных
        swipeAction.setOnRefreshListener {   presenter.selectionLoad()  }



        //setup recyclerView
        manager = LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        adapter = AdapterNewsList(this)

        recyclerViewNewsList.layoutManager = manager
        recyclerViewNewsList.adapter = adapter
    }




    // toolbar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.news_list_menu, menu)

        // инициализируем кнопки
        button_sort_item = menu.findItem(R.id.sort_item)
        button_clear_data = menu.findItem(R.id.clear_data_base_item)

        super.onCreateOptionsMenu(menu, inflater)


        // только при первом открытии программы
        if (presenter.newsList.isNullOrEmpty()){
            showButtonToolbar(false)
        }
        else{
            showButtonToolbar(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.sort_item -> {  showSortScreen()  }                        // Dialog с категориями для сортировки

            R.id.clear_data_base_item -> {  showDialogForClearDB()  }       // Dialog с вопросом о удалении
        }
        return super.onOptionsItemSelected(item)
    }




    //______________________________
    //view implementation

    // управление показом прогресса
    override fun showProgress(show: Boolean) {
        swipeAction.isRefreshing = show
    }

    // отображение списка новостей
    override fun showNewsList(newsLIst: LinkedList<NewsModel>) {
        textViewMessage.visibility = View.GONE
        adapter.setupAdapter(newsLIst)
    }

    override fun showMessage(message: Int) {
        Toast.makeText(activity, getText(message), Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }


    // переход к фрагменту деталировки и передача выбранной новости
    override fun onClick(view: View, news: NewsModel) {

        val newsBundle: Bundle = bundleOf("selectedNewsItem" to news)
        findNavController().navigate(R.id.action_to_newsDetailsFragment, newsBundle)
    }


    // отображение категорий сортировки
    override fun showSortScreen() {

        val categoryList = resources.getStringArray(R.array.title_category_list)

        AlertDialog.Builder(activity)
            .setTitle(R.string.text_category)
            .setItems(categoryList) { _, which ->

                // передаем презентеру выбранную категорию
                presenter.sortingNewsList(categoryList[which], categoryList[0])
            }
            .create()
            .show()
    }


    // показ сообщений в textView на главном экране
    override fun showTextViewMessage(message: Int) {

        textViewMessage.visibility = View.VISIBLE
        textViewMessage.text = getString(message)
    }

    override fun showTextViewMessage(message: String) {
        textViewMessage.visibility = View.VISIBLE
        textViewMessage.text = message
    }


    override fun showDialogForClearDB() {

        AlertDialog.Builder(activity!!)
            .setTitle(R.string.question_delete_all_from_db)

            .setPositiveButton(R.string.answer_yes) { _, _ ->
                presenter.clearDateBase()

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


    // скрытие/отображение кнопок в тулбаре
    override fun showButtonToolbar(show: Boolean) {
        button_sort_item.isVisible = show
        button_clear_data.isVisible = show
    }




    //save scroll position

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("recState", manager.onSaveInstanceState())
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null){
            manager.onRestoreInstanceState(savedInstanceState.getParcelable("recState"))
        }
    }

}
