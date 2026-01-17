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
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    // 数据库迁移策略
    companion object {
        val MIGRATION_1_2 = androidx.room.migration.Migration(1, 2) {
            // 向dailyrecord表添加color列
            it.execSQL("ALTER TABLE dailyrecord ADD COLUMN color TEXT")
        }
    }
    
    val repository by lazy {
        AppRepository(database)
    }

    override fun onCreate() {
        super.onCreate()
    }
}