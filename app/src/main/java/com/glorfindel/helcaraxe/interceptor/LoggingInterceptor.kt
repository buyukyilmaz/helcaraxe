package com.glorfindel.helcaraxe.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.nio.charset.StandardCharsets

class LoggingInterceptor(
    private val requestTag: String = "helcaraxe request",
    private val responseTag: String = "helcaraxe response",
    private val requestHeaderLogType: HeaderLogType = HeaderLogType.REQUEST,
    private val responseHeaderLogType: HeaderLogType = HeaderLogType.NONE,
    private val customTags: Map<String, String> = mapOf()
) : Interceptor {
    enum class HeaderLogType {
        NONE,
        REQUEST,
        RESPONSE
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val startTime = System.nanoTime()
        val request = chain.request()
        logRequest(request)
        val response =
            try {
                chain.proceed(request)
            } catch (e: Exception) {
                Log.println(Log.DEBUG, responseTag, "HTTP Failed: $e")
                throw e
            }
        logResponse(response, startTime)
        return response
    }

    private fun logRequest(request: Request) {
        val newLine = System.lineSeparator()
        var log = "─────────────────────────────────REQUEST─────────────────────────────────"
        val url = request.url.toString().substringBefore('?')
        log += "${newLine}URL: ${request.method} $url"

        request.url.query?.let { log += "${newLine}Query: $it" }

        if (requestHeaderLogType == HeaderLogType.REQUEST && request.headers.size > 0) {
            log += "$newLine-----------------------Headers-----------------------"
            request.headers.forEach { log += "${newLine}${it.first}: ${it.second}" }
            log += "$newLine-----------------------Headers-----------------------"
        }

        request.body?.let {
            val requestBuffer = Buffer()
            it.writeTo(requestBuffer)
            log += "${newLine}Body: ${requestBuffer.readUtf8()}"
        }
        log += "$newLine───────────────────────────────END REQUEST───────────────────────────────"

        val customTag = customTags.keys.find { url.contains(it) }
        val tag = if (customTag == null) requestTag else "$requestTag [${customTags[customTag]}]"

        Log.println(Log.DEBUG, tag, log)
    }

    private fun logResponse(
        response: Response,
        startTime: Long
    ) {
        val newLine = System.lineSeparator()
        val diff = (System.nanoTime() - startTime) / 1000000
        val url = response.request.url.toString().substringBefore('?')
        var log = "─────────────────────────────────RESPONSE─────────────────────────────────"
        log += "${newLine}Response Time: $diff millisecond"
        log += "${newLine}HTTP Status Code: ${response.code}"
        log += "${newLine}URL: $url"
        response.request.url.query?.let { log += "${newLine}Query: $it" }
        when (responseHeaderLogType) {
            HeaderLogType.NONE -> {}
            HeaderLogType.REQUEST -> {
                if (response.request.headers.size > 0) {
                    log += "$newLine-----------------------Headers-----------------------"
                    response.request.headers.forEach { log += "${newLine}${it.first}: ${it.second}" }
                    log += "$newLine-----------------------Headers-----------------------"
                }
            }

            HeaderLogType.RESPONSE -> {
                if (response.headers.size > 0) {
                    log += "$newLine-----------------------Headers-----------------------"
                    response.headers.forEach { log += "${newLine}${it.first}: ${it.second}" }
                    log += "$newLine-----------------------Headers-----------------------"
                }
            }
        }
        response.body?.let {
            val source = it.source()
            source.request(Long.MAX_VALUE)
            var buffer = source.buffer
            if ("gzip".equals(response.headers["Content-Encoding"], ignoreCase = true)) {
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }
            val contentType = it.contentType()
            val charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
            log += "${newLine}Body: ${buffer.clone().readString(charset)}"
        }
        log += "$newLine───────────────────────────────END RESPONSE───────────────────────────────"

        val customTag = customTags.keys.find { url.contains(it) }
        val tag = if (customTag == null) responseTag else "$responseTag [${customTags[customTag]}]"

        Log.println(Log.DEBUG, tag, log)
    }
}
