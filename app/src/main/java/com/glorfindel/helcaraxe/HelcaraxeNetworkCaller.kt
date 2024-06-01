package com.glorfindel.helcaraxe

import com.glorfindel.helcaraxe.utils.CallerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HelcaraxeNetworkCaller<T> private constructor(
    private val call: (suspend () -> T)?,
    private val success: ((T) -> Unit)?,
    private val failure: ((HttpException) -> Unit)?,
    private val exception: ((Exception) -> Unit)?
) {
    fun launchIn(scope: CoroutineScope) {
        if (call == null) return

        scope.launch(Dispatchers.IO) {
            val callerHelper = CallerHelper()
            callerHelper.execute(this, call, success, failure, exception)
        }
    }

    companion object {
        fun <T> build(lambda: Builder<T>.() -> Unit): HelcaraxeNetworkCaller<T> {
            val builder = Builder<T>()
            builder.lambda()
            return builder.build()
        }
    }

    class Builder<T> {
        private var call: (suspend () -> T)? = null
        private var success: ((T) -> Unit)? = null
        private var failure: ((HttpException) -> Unit)? = null
        private var exception: ((Exception) -> Unit)? = null

        fun call(call: suspend () -> T) {
            this.call = call
        }

        fun success(success: (T) -> Unit) {
            this.success = success
        }

        fun failure(failure: (HttpException) -> Unit) {
            this.failure = failure
        }

        fun exception(exception: (Exception) -> Unit) {
            this.exception = exception
        }

        fun build(): HelcaraxeNetworkCaller<T> = HelcaraxeNetworkCaller(call, success, failure, exception)
    }
}
