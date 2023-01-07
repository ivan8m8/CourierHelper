package io.github.ivan8m8.courierhelper.data.di

import androidx.room.Room
import io.github.ivan8m8.courierhelper.data.db.AppDb
import io.github.ivan8m8.courierhelper.data.repository.AutocompleteRepository
import io.github.ivan8m8.courierhelper.data.repository.DeliveriesRepository
import io.github.ivan8m8.courierhelper.ui.viewmodels.AddDeliveryViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
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
        single {
            DeliveriesRepository(get(), androidApplication())
        }
        single {
            AutocompleteRepository(get())
        }
        viewModel {
            AddDeliveryViewModel(get(), get())
        }
    }
}