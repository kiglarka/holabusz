package com.codecool.holabusz.network

import okhttp3.OkHttpClient
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
            val client = OkHttpClient()

            if (retrofit == null) {

                retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                        RxJava2CallAdapterFactory.create()
                    )
                    .addConverterFactory(
                        GsonConverterFactory.create()
                    )
                    .baseUrl(BASE_URL)
                    .client(client)
                    .build()

            }


            return retrofit!!
        }

        fun getRequestApi(): RequestApi {
            return RetrofitClient.getClient().create(RequestApi::class.java)
        }
    }

}
