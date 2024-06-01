package com.glorfindel.helcaraxe.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.glorfindel.helcaraxe.common.enums.NetworkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object NetworkUtils {
    // you need to add
    // <uses-permission name="android.permission.ACCESS_NETWORK_STATE" />
    @SuppressLint("MissingPermission")
    fun getNetworkStatus(context: Context): NetworkStatus {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return NetworkStatus.NO_NETWORK
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return NetworkStatus.NO_NETWORK
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkStatus.WIFI
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkStatus.CELLULAR
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkStatus.ETHERNET
            else -> NetworkStatus.NO_NETWORK
        }
    }

    fun ping(
        url: String = "https://www.google.com",
        timeout: Long = 3000L
    ) = flow {
        try {
            val client =
                OkHttpClient.Builder()
                    .callTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build()

            val request =
                Request.Builder()
                    .url(url)
                    .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                emit(true)
            } else {
                emit(false)
            }
        } catch (e: Exception) {
            emit(false)
        }
    }.flowOn(Dispatchers.IO)
}
