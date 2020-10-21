package com.codecool.holabusz.app
import com.codecool.holabusz.main.MainPresenter
import com.codecool.holabusz.network.EncodingInterceptor
import com.codecool.holabusz.network.RequestApi
import com.codecool.holabusz.network.RetrofitClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val MainModule = module {
    single { MainPresenter() }
}


val NetworkModule = module {
    single { HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.HEADERS) }

    single {
        EncodingInterceptor()
    }

    single<GsonConverterFactory> {
        GsonConverterFactory.create()
    }

    single {
        RxJava2CallAdapterFactory.create()
    }

    single<OkHttpClient> {
        OkHttpClient().newBuilder()
            .addInterceptor(get<EncodingInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .addCallAdapterFactory(get<RxJava2CallAdapterFactory>())
            .addConverterFactory(get<GsonConverterFactory>())
            .baseUrl(RetrofitClient.BASE_URL)
            .client(get<OkHttpClient>())
            .build()
    }

    single<RequestApi> {
        get<Retrofit>().create(RequestApi::class.java)
    }
}


