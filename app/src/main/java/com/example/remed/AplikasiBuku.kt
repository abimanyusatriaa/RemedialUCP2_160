package com.example.remed

import android.app.Application
import com.example.remed.repositori.AppContainer
import com.example.remed.repositori.ContainerApp

class AplikasiBuku : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = ContainerApp(this)
    }
}
