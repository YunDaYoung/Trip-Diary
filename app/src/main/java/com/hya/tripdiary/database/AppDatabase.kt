package com.hya.tripdiary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CashBook::class, Diary::class, Map::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun cashBookDao(): CashBookDao
    abstract fun diaryDao(): DiaryDao
    abstract fun mapDao(): MapDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "tripdiary-db").fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}