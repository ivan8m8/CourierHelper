package io.github.ivan8m8.courierhelper.data.network

import okhttp3.OkHttpClient

object OkHttpClient {
    val instance by lazy {
        OkHttpClient.Builder()
            .build()
    }
}