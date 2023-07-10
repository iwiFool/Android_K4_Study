package com.bignerdranch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.UUID

// 为 CrimeFragment 添加 ViewModel
class CrimeDetailViewModel() : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    // Transformation， LiveData 数据转换
    // 一个数据转换函数需要两个参数：一个用作触发器(trigger)的 LiveData 对象，一个返回 LiveData 对象的映射函数(mapping function)
    // 数据转换函数会返回一个 数据转换结果(transformation result)——其实就是一个 LiveData 对象。
    // 每次只要触发器 LiveData 有新值设置，数据转换函数返回的新 LiveData 对象的值就会得到更新。
    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime) {
        crimeRepository.update(crime)
    }

    // 通过 CrimeDetailViewModel 展示文件信息
    fun getPhotoFile(crime: Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }
}