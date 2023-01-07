package io.github.ivan8m8.courierhelper.data.network.interceptors

import io.github.ivan8m8.courierhelper.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class KladrApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .url(
                    chain.request().url.newBuilder()
                        .addQueryParameter("token", BuildConfig.KLADR_TOKEN)
                        .build()
                )
                .build()
        )
    }
}