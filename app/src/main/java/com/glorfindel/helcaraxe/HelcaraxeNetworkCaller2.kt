package com.glorfindel.helcaraxe

import com.glorfindel.helcaraxe.utils.CallerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HelcaraxeNetworkCaller2<T1, T2> private constructor(
    private val call1: (suspend () -> T1)?,
    private val call2: (suspend () -> T2)?,
    private val success: ((T1, T2) -> Unit)?,
    private val failure: ((HttpException) -> Unit)?,
    private val exception: ((Exception) -> Unit)?
) {
    fun launchIn(scope: CoroutineScope) {
        if (call1 == null || call2 == null) return

        var response1: T1? = null
        var response2: T2? = null
        var failure1: HttpException? = null
        var failure2: HttpException? = null
        var exception1: Exception? = null
        var exception2: Exception? = null
        val callerHelper = CallerHelper()

        scope.launch(Dispatchers.IO) {
            val j1 = async { callerHelper.execute(this, call1, success = { response1 = it }, failure = { failure1 = it }, exception = { exception1 = it }) }
            val j2 = async { callerHelper.execute(this, call2, success = { response2 = it }, failure = { failure2 = it }, exception = { exception2 = it }) }
            awaitAll(j1, j2)
            callerHelper.checkStatus(this, listOf(failure1, failure2), listOf(exception1, exception2), failure, exception) {
                success?.invoke(response1!!, response2!!)
            }
        }
    }

    companion object {
        fun <T1, T2> build(lambda: Builder<T1, T2>.() -> Unit): HelcaraxeNetworkCaller2<T1, T2> {
            val builder = Builder<T1, T2>()
            builder.lambda()
            return builder.build()
        }
    }

    class Builder<T1, T2> {
        private var call1: (suspend () -> T1)? = null
        private var call2: (suspend () -> T2)? = null
        private var success: ((T1, T2) -> Unit)? = null
        private var failure: ((HttpException) -> Unit)? = null
        private var exception: ((Exception) -> Unit)? = null

        fun call1(call1: suspend () -> T1) {
            this.call1 = call1
        }

        fun call2(call2: suspend () -> T2) {
            this.call2 = call2
        }

        fun success(success: (T1, T2) -> Unit) {
            this.success = success
        }

        fun failure(failure: (HttpException) -> Unit) {
            this.failure = failure
        }

        fun exception(exception: (Exception) -> Unit) {
            this.exception = exception
        }

        fun build(): HelcaraxeNetworkCaller2<T1, T2> = HelcaraxeNetworkCaller2(call1, call2, success, failure, exception)
    }
}
