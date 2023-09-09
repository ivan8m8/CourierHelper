package io.github.ivan8m8.courierhelper.data.di

import androidx.work.WorkManager
import io.github.ivan8m8.courierhelper.data.workers.FetchMetroStationsWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

object WorkerModule {
    val workerModule = module {
        worker {
            FetchMetroStationsWorker(
                get(),
                get(),
                get()
            )
        }
        single {
            WorkManager.getInstance(androidContext())
        }
    }
}