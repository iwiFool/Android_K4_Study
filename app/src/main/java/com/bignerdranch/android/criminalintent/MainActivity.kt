package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.UUID

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actitvity_main)

        // 添加一个 CrimeFragment
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
//            val fragment = CrimeFragment()
            // 使用 fragment 事务
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment) // 创建并提交了一个 fragment 事务
                .commit()
        }
    }

    override fun onCrimeSelected(crimeId: UUID) {
//        Log.d(TAG, "MainActivity.onCrimeSelected: $crimeId")
        // 用 CrimeFragment 替换 CrimeListFragment
//        val fragment = CrimeFragment()
        // 使用 CrimeFragment.newInstance(UUID) 函数
        val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            // 把fragment 事务添加到 回退栈
                // 把一个事务添加到回退栈后，在用户按回退键时，事务会回滚。因此，在这种情况下，CrimeFragment 又被替换回了 CrimeListFragment
            .addToBackStack(null)
            .commit()
    }
}