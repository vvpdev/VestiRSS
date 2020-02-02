package com.vvp.vestirss.activities

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.arellomobile.mvp.MvpAppCompatActivity
import com.vvp.vestirss.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : MvpAppCompatActivity() {


    private lateinit var navigationController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(toolbar)


        navigationController = Navigation.findNavController(this, R.id.navGraphHost)
        NavigationUI.setupActionBarWithNavController(this, navigationController)

        val appBarConfiguration = AppBarConfiguration(navigationController.graph)
        toolbar.setupWithNavController(navigationController, appBarConfiguration)
    }


}
