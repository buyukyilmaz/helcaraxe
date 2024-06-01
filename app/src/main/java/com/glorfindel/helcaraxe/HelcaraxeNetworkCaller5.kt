package com.glorfindel.helcaraxe

import com.glorfindel.helcaraxe.utils.CallerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HelcaraxeNetworkCaller5<T1, T2, T3, T4, T5> private constructor(
    private val call1: (suspend () -> T1)?,
    private val call2: (suspend () -> T2)?,
    private val call3: (suspend () -> T3)?,
    private val call4: (suspend () -> T4)?,
    private val call5: (suspend () -> T5)?,
    private val success: ((T1, T2, T3, T4, T5) -> Unit)?,
    private val failure: ((HttpException) -> Unit)?,
    private val exception: ((Exception) -> Unit)?
) {
    fun launchIn(scope: CoroutineScope) {
        if (call1 == null || call2 == null || call3 == null || call4 == null || call5 == null) return

        var response1: T1? = null
        var response2: T2? = null
        var response3: T3? = null
        var response4: T4? = null
        var response5: T5? = null
        var failure1: HttpException? = null
        var failure2: HttpException? = null
        var failure3: HttpException? = null
        var failure4: HttpException? = null
        var failure5: HttpException? = null
        var exception1: Exception? = null
        var exception2: Exception? = null
        var exception3: Exception? = null
        var exception4: Exception? = null
        var exception5: Exception? = null
        val callerHelper = CallerHelper()

        scope.launch(Dispatchers.IO) {
            val j1 = async { callerHelper.execute(this, call1, success = { response1 = it }, failure = { failure1 = it }, exception = { exception1 = it }) }
            val j2 = async { callerHelper.execute(this, call2, success = { response2 = it }, failure = { failure2 = it }, exception = { exception2 = it }) }
            val j3 = async { callerHelper.execute(this, call3, success = { response3 = it }, failure = { failure3 = it }, exception = { exception3 = it }) }
            val j4 = async { callerHelper.execute(this, call4, success = { response4 = it }, failure = { failure4 = it }, exception = { exception4 = it }) }
            val j5 = async { callerHelper.execute(this, call5, success = { response5 = it }, failure = { failure5 = it }, exception = { exception5 = it }) }
            awaitAll(j1, j2, j3, j4, j5)
            callerHelper.checkStatus(this, listOf(failure1, failure2, failure3, failure4, failure5), listOf(exception1, exception2, exception3, exception4, exception5), failure, exception) {
                success?.invoke(response1!!, response2!!, response3!!, response4!!, response5!!)
            }
        }
    }

    companion object {
        fun <T1, T2, T3, T4, T5> build(lambda: Builder<T1, T2, T3, T4, T5>.() -> Unit): HelcaraxeNetworkCaller5<T1, T2, T3, T4, T5> {
            val builder = Builder<T1, T2, T3, T4, T5>()
            builder.lambda()
            return builder.build()
        }
    }

    class Builder<T1, T2, T3, T4, T5> {
        private var call1: (suspend () -> T1)? = null
        private var call2: (suspend () -> T2)? = null
        private var call3: (suspend () -> T3)? = null
        private var call4: (suspend () -> T4)? = null
        private var call5: (suspend () -> T5)? = null
        private var success: ((T1, T2, T3, T4, T5) -> Unit)? = null
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

        fun call4(call4: suspend () -> T4) {
            this.call4 = call4
        }

        fun call5(call5: suspend () -> T5) {
            this.call5 = call5
        }

        fun success(success: (T1, T2, T3, T4, T5) -> Unit) {
            this.success = success
        }

        fun failure(failure: (HttpException) -> Unit) {
            this.failure = failure
        }

        fun exception(exception: (Exception) -> Unit) {
            this.exception = exception
        }

        fun build(): HelcaraxeNetworkCaller5<T1, T2, T3, T4, T5> = HelcaraxeNetworkCaller5(call1, call2, call3, call4, call5, success, failure, exception)
    }
}
