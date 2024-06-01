package com.glorfindel.helcaraxe.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

internal class CallerHelper {
    suspend fun <T> execute(
        scope: CoroutineScope,
        request: suspend () -> T,
        success: ((T) -> Unit)? = null,
        failure: ((HttpException) -> Unit)? = null,
        exception: ((Exception) -> Unit)? = null
    ) {
        try {
            val response = request.invoke()
            scope.launch(Dispatchers.Main) { success?.invoke(response) }
        } catch (e: HttpException) {
            scope.launch(Dispatchers.Main) { failure?.invoke(e) }
        } catch (e: IOException) {
            scope.launch(Dispatchers.Main) { exception?.invoke(e) }
        } catch (e: Exception) {
            scope.launch(Dispatchers.Main) { exception?.invoke(e) }
        }
    }

    fun checkStatus(
        scope: CoroutineScope,
        failures: List<HttpException?>,
        exceptions: List<Exception?>,
        failure: ((HttpException) -> Unit)?,
        exception: ((Exception) -> Unit)?,
        success: () -> Unit
    ) {
        val f = failures.find { it != null }
        val e = exceptions.find { it != null }
        scope.launch(Dispatchers.Main) {
            if (f != null) {
                failure?.invoke(f)
            } else if (e != null) {
                exception?.invoke(e)
            } else {
                success()
            }
        }
    }
}
