package com.vvp.vestirss

import android.app.Application
import android.content.Context
import com.vvp.vestirss.di.components.DIComponent
import com.vvp.vestirss.di.components.DaggerDIComponent

class App: Application() {


    companion object{

        var diComponent: DIComponent? = null
        var context: Context? = null
    }


    override fun onCreate() {
        super.onCreate()

        diComponent = DaggerDIComponent.builder().build()
        context = this
    }
}