package com.glorfindel.helcaraxe

import com.glorfindel.helcaraxe.utils.CallerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HelcaraxeNetworkCaller3<T1, T2, T3> private constructor(
    private val call1: (suspend () -> T1)?,
    private val call2: (suspend () -> T2)?,
    private val call3: (suspend () -> T3)?,
    private val success: ((T1, T2, T3) -> Unit)?,
    private val failure: ((HttpException) -> Unit)?,
    private val exception: ((Exception) -> Unit)?
) {
    fun launchIn(scope: CoroutineScope) {
        if (call1 == null || call2 == null || call3 == null) return

        var response1: T1? = null
        var response2: T2? = null
        var response3: T3? = null
        var failure1: HttpException? = null
        var failure2: HttpException? = null
        var failure3: HttpException? = null
        var exception1: Exception? = null
        var exception2: Exception? = null
        var exception3: Exception? = null
        val callerHelper = CallerHelper()

        scope.launch(Dispatchers.IO) {
            val j1 = async { callerHelper.execute(this, call1, success = { response1 = it }, failure = { failure1 = it }, exception = { exception1 = it }) }
            val j2 = async { callerHelper.execute(this, call2, success = { response2 = it }, failure = { failure2 = it }, exception = { exception2 = it }) }
            val j3 = async { callerHelper.execute(this, call3, success = { response3 = it }, failure = { failure3 = it }, exception = { exception3 = it }) }
            awaitAll(j1, j2, j3)
            callerHelper.checkStatus(this, listOf(failure1, failure2, failure3), listOf(exception1, exception2, exception3), failure, exception) {
                success?.invoke(response1!!, response2!!, response3!!)
            }
        }
    }

    companion object {
        fun <T1, T2, T3> build(lambda: Builder<T1, T2, T3>.() -> Unit): HelcaraxeNetworkCaller3<T1, T2, T3> {
            val builder = Builder<T1, T2, T3>()
            builder.lambda()
            return builder.build()
        }
    }

    class Builder<T1, T2, T3> {
        private var call1: (suspend () -> T1)? = null
        private var call2: (suspend () -> T2)? = null
        private var call3: (suspend () -> T3)? = null
        private var success: ((T1, T2, T3) -> Unit)? = null
        private var failure: ((HttpException) -> Unit)? = null
        private var exception: ((Exception) -> Unit)? = null

        fun call1(call1: suspend () -> T1) {
            this.call1 = call1
        }

        fun call2(call2: suspend () -> T2) {
            this.call2 = call2
        }

        fun call3(call3: suspend () -> T3) {
            this.call3 = call3
        }

        fun success(success: (T1, T2, T3) -> Unit) {
            this.success = success
        }

        fun failure(failure: (HttpException) -> Unit) {
            this.failure = failure
        }

        fun exception(exception: (Exception) -> Unit) {
            this.exception = exception
        }

        fun build(): HelcaraxeNetworkCaller3<T1, T2, T3> = HelcaraxeNetworkCaller3(call1, call2, call3, success, failure, exception)
    }
}
