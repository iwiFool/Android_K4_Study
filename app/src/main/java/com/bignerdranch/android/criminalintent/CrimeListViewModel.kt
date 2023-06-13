package com.bignerdranch.android.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {

    // 在 ViewModel 里访问仓库
    private val crimeRepository = CrimeRepository.get()
//    val crimes = crimeRepository.getCrimes()
    val crimeListLiveData = crimeRepository.getCrimes()
}