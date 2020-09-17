package com.codecool.holabusz.network

import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        const val BASE_URL = "https://futar.bkk.hu/api/query/v1/ws/otp/api/where/"
        var retrofit: Retrofit? = null


        fun getClient(): Retrofit {

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val okhttpClient =
                OkHttpClient().newBuilder()
                    .addInterceptor(EncodingInterceptor())
                    .addInterceptor(interceptor)
                    .build()

            if (retrofit == null) {

                retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(okhttpClient)
                    .build()
            }
            return retrofit!!
        }

        fun getRequestApi(): RequestApi {
            return RetrofitClient.getClient().create(RequestApi::class.java)
        }
    }
}

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
