package com.bignerdranch.android.criminalintent

import android.app.Application

// 创建 Application 子类，
// 覆盖 Application.onCreate() 函数进行 CrimeRepository 类的初始化
class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}