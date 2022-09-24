package io.github.ivan8m8.courierhelper.data.di

import io.github.ivan8m8.courierhelper.data.network.KladrApi
import io.github.ivan8m8.courierhelper.data.network.KladrApiInterceptor
import io.github.ivan8m8.courierhelper.data.network.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    val networkModule = module {
        single {
            Retrofit.Builder()
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.instance.newBuilder()
                        .addInterceptor(
                            KladrApiInterceptor()
                        )
                        .addInterceptor(
                            HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY)
                        )
                        .build()
                )
                .baseUrl(KladrApi.BASE_URL)
                .build()
                .create(KladrApi::class.java)
        }
    }
}