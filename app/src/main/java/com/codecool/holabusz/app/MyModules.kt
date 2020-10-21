package com.codecool.holabusz.app
import com.codecool.holabusz.main.MainPresenter
import org.koin.dsl.module

val MainModule = module {
    single { MainPresenter() }
}



