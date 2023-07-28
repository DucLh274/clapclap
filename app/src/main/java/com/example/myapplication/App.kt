package com.example.myapplication

import android.app.Application
import android.util.Log
import xyz.kumaraswamy.autostart.Autostart

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        ClapRecorder.init(this)

    }
}