package com.codecool.holabusz.app

import com.codecool.holabusz.main.MainContract
import com.codecool.holabusz.main.MainPresenter
import com.codecool.holabusz.network.RequestApi
import com.codecool.holabusz.network.RetrofitClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://futar.bkk.hu/api/query/v1/ws/otp/api/where/"

val myAppModules = module {

    single<MainPresenter> { MainPresenter() }

    single<Retrofit> {
        Retrofit.Builder()
            .client(get())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    single{ get<Retrofit>().create(RequestApi::class.java) }

}