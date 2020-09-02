package com.codecool.holabusz.app

import com.codecool.holabusz.main.MainContract
import com.codecool.holabusz.main.MainPresenter
import org.koin.dsl.module

val myAppModules = module {

    single<MainPresenter> { (view: MainContract.MainView) -> MainPresenter(view) }
}