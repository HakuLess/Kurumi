package com.haku.kurumi.base

import android.app.Application

class KurumiApplication : Application() {

    companion object {
        lateinit var instance: KurumiApplication
    }

    override fun onCreate() {
        super.onCreate()
        // 初始化 myObject 对象
        instance = this
    }
}