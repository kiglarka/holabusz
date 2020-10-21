package com.codecool.holabusz.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class EncodingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var string = request.url().toString()
        string = string.replace("%26", "&").replace("%3D", "=")

        val newRequest = Request.Builder()
            .url(string)
            .build()
        return chain.proceed(newRequest)
    }
}