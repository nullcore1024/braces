package com.example.braces2

import android.app.Application
import androidx.room.Room
import com.example.braces2.data.AppDatabase
import com.example.braces2.data.AppRepository

class BracesApplication : Application() {
    // 数据库和仓库的懒加载单例
    val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "braces.db"
        )
            .build()
    }
    
    val repository by lazy {
        AppRepository(database)
    }

    override fun onCreate() {
        super.onCreate()
    }
}