package io.github.ivan8m8.courierhelper.data.network.interceptors

import io.github.ivan8m8.courierhelper.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class DadataInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("Authorization", "Token " + BuildConfig.DADATA_TOKEN)
                .build()
        )
    }
}