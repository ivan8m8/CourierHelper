package io.github.ivan8m8.courierhelper.data.di

import io.github.ivan8m8.courierhelper.data.network.DadataApi
import io.github.ivan8m8.courierhelper.data.network.interceptors.DadataInterceptor
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
                            DadataInterceptor()
                        )
                        .build()
                )
                .baseUrl(DadataApi.BASE_URL)
                .build()
                .create(DadataApi::class.java)
        }
    }
}