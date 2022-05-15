package io.github.ivan8m8.courierhelper.data.di

import androidx.room.Room
import io.github.ivan8m8.courierhelper.data.db.AppDb
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

object MainModule {
    val mainModule = module {
        single {
            Room.databaseBuilder(
                androidApplication(),
                AppDb::class.java,
                AppDb.NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        single {
            get<AppDb>().deliveriesDao()
        }
    }
}