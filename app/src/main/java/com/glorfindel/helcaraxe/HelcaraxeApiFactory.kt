package com.glorfindel.helcaraxe

import com.glorfindel.helcaraxe.interceptor.LoggingInterceptor
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class HelcaraxeApiFactory {
    open fun getBaseUrl(): String = ""

    open fun getHeaders(request: Request): MutableMap<String, String>? = null

    open fun connectTimeout(): Long = 60L

    open fun readTimeout(): Long = 60L

    open fun writeTimeout(): Long = 60L

    open fun addLoggingInterceptor(): LoggingInterceptor? = null

    open fun getSsl(): CertificatePinner? = null

    fun <T> getApi(api: Class<T>) = getRetrofit().create(api)

    private fun getRetrofit() =
        Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHttpClient())
            .build()

    private fun getHttpClient(): OkHttpClient {
        val okHttpClientBuilder =
            OkHttpClient.Builder()
                .connectTimeout(connectTimeout(), TimeUnit.SECONDS)
                .readTimeout(readTimeout(), TimeUnit.SECONDS)
                .writeTimeout(writeTimeout(), TimeUnit.SECONDS)

        addHeaderInterceptor(okHttpClientBuilder)

        addLoggingInterceptor()?.let { okHttpClientBuilder.addInterceptor(it) }

        getSsl()?.let { okHttpClientBuilder.certificatePinner(it) }

        return okHttpClientBuilder.build()
    }

    private fun addHeaderInterceptor(okHttpClientBuilder: OkHttpClient.Builder) {
        okHttpClientBuilder.addInterceptor {
            val original = it.request()
            val headerMap = getHeaders(original) ?: return@addInterceptor it.proceed(original)

            val requestBuilder = original.newBuilder()
            headerMap.forEach { item ->
                requestBuilder.header(item.key, item.value)
            }
            val request = requestBuilder.method(original.method, original.body).build()
            return@addInterceptor it.proceed(request)
        }
    }
}
