package com.bignerdranch.android.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.criminalintent.Crime

// 新增 crime 数据库字段，需要增加 CrimeDatabase 类的版本号，以及告诉 Room 如何在不同版本间迁移数据库
//@Database(entities = [Crime::class], version = 1)
@Database(entities = [Crime::class], version = 2)

@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {

    // 在数据库类里登记 DAO
    abstract fun crimeDao(): CrimeDao
}

// 添加数据库迁移类
val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}