package com.giraffe.qiblaapp.core.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.giraffe.qiblaapp.ui.screens.qibla.QiblaViewModel
val appModule = module {
    viewModelOf(::QiblaViewModel)
}