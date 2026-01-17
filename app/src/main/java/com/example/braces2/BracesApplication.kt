package com.example.braces2

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import java.util.Locale
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
        
        // 设置区域设置，将星期一作为星期的第一天
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.Builder().setLanguage("zh").setRegion("CN").build()
        } else {
            Locale("zh", "CN") // 兼容旧版本
        }
        
        Locale.setDefault(locale)
        
        val config = Configuration(resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            createConfigurationContext(config)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        } else {
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}