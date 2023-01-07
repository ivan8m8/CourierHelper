package io.github.ivan8m8.courierhelper.data.di

import io.github.ivan8m8.courierhelper.data.network.GeoTreeApi
import io.github.ivan8m8.courierhelper.data.network.KladrApi
import io.github.ivan8m8.courierhelper.data.network.interceptors.GeoTreeInterceptor
import io.github.ivan8m8.courierhelper.data.network.interceptors.KladrApiInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    val networkModule = module {
        single {
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build()
        }
        single {
            Retrofit.Builder()
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    get<OkHttpClient>().newBuilder()
                        .addInterceptor(
                            GeoTreeInterceptor()
                        )
                        .build()
                )
                .baseUrl(GeoTreeApi.BASE_URL)
                .build()
                .create(GeoTreeApi::class.java)
        }
        single {
            Retrofit.Builder()
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    get<OkHttpClient>().newBuilder()
                        .addInterceptor(
                            KladrApiInterceptor()
                        )
                        .build()
                )
                .baseUrl(KladrApi.BASE_URL)
                .build()
                .create(KladrApi::class.java)
        }
    }
}