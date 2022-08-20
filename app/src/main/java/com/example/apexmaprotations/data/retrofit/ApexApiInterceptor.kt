package com.example.apexmaprotations.data.retrofit

import com.example.apexmaprotations.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

//  Add Api key to each call
class ApexApiInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()
        val url = original.url().newBuilder().addQueryParameter("auth", BuildConfig.ALS_KEY)
            .addQueryParameter("version", "2").build()
        original = original.newBuilder().url(url).build()
        return chain.proceed(original)
    }
}