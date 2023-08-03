package io.github.ivan8m8.courierhelper.data.di

import android.content.Context
import androidx.room.Room
import io.github.ivan8m8.courierhelper.data.db.AppDb
import io.github.ivan8m8.courierhelper.data.repository.AutocompleteRepository
import io.github.ivan8m8.courierhelper.data.repository.DeliveriesRepository
import io.github.ivan8m8.courierhelper.data.repository.GeoCodeRepository
import io.github.ivan8m8.courierhelper.data.utils.RegionPrefUtils
import io.github.ivan8m8.courierhelper.data.utils.SharedPrefsKeys
import io.github.ivan8m8.courierhelper.ui.viewmodels.AddDeliveryViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object MainModule {
    val mainModule = module {
        viewModel {
            AddDeliveryViewModel(get(), get(), get(), androidApplication())
        }
        single {
            RegionPrefUtils(get())
        }
        single {
            GeoCodeRepository(get())
        }
        single {
            AutocompleteRepository(get())
        }
        single {
            DeliveriesRepository(get())
        }
        single {
            get<AppDb>().deliveriesDao()
        }
        single {
            androidApplication().getSharedPreferences(SharedPrefsKeys.NAME, Context.MODE_PRIVATE)
        }
        single {
            Room.databaseBuilder(
                androidApplication(),
                AppDb::class.java,
                AppDb.NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}