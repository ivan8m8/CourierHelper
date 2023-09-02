package io.github.ivan8m8.courierhelper

import android.app.Application
import io.github.ivan8m8.courierhelper.data.di.MainModule
import io.github.ivan8m8.courierhelper.data.di.NetworkModule
import io.github.ivan8m8.courierhelper.data.di.WorkerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            workManagerFactory()
            modules(
                MainModule.mainModule,
                NetworkModule.networkModule,
                WorkerModule.workerModule
            )
        }
    }
}