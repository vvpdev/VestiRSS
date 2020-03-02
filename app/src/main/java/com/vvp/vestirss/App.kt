package com.vvp.vestirss

import android.app.Application
import com.vvp.vestirss.di.components.DIComponent
import com.vvp.vestirss.di.components.DaggerDIComponent
import com.vvp.vestirss.di.modules.DateBaseModule

class App: Application() {


    companion object{

        var diComponent: DIComponent? = null
    }


    override fun onCreate() {
        super.onCreate()

        diComponent = DaggerDIComponent.builder().dateBaseModule(DateBaseModule(context = applicationContext)).build()
    }
}