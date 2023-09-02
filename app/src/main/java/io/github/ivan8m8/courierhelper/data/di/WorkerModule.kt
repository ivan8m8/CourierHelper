package io.github.ivan8m8.courierhelper.data.di

import io.github.ivan8m8.courierhelper.data.workers.FetchMetroStationsWorker
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
    }
}