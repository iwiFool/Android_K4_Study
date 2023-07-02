package com.bignerdranch.android.criminalintent

import android.content.Context
import android.content.LocusId
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.criminalintent.database.CrimeDatabase
import java.lang.IllegalStateException
import java.util.UUID
import java.util.concurrent.Executors

/**
 * 仓库类
 *      仓库类封装了从单个或多个数据源访问数据的一套逻辑。它决定如何读取和保存数据；
 *      UI代码直接从仓库获得要使用的数据，不关心如何与数据库打交道
 */
private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context) {

    // 配置仓库属性
    private val database : CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val crimeDao = database.crimeDao()

    private val executor = Executors.newSingleThreadExecutor()

    // 添加仓库函数
    // 通过仓库调用 Dao 中的操作函数
//    fun getCrimes(): List<Crime> = crimeDao.getCrimes()
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
//    fun getCrime(id: UUID): Crime? = crimeDao.getCrime(id)
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    fun update(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    // 使成为单例
    companion object {
        private var INSTANCES: CrimeRepository? = null
        // 1. 初始化生成仓库新实例
        fun initialize(context: Context) {

            if (INSTANCES == null) {
                INSTANCES = CrimeRepository(context)
            }
        }

        // 2. 读取仓库数据
        fun get(): CrimeRepository {
            return INSTANCES ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}