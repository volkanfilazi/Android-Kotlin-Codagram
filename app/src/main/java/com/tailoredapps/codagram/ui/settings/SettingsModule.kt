package com.tailoredapps.codagram.ui.settings

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val SettingsModule = module {
    fragment { SettingsFragment() }
    viewModel { SettingsViewModel(codaGramApi = get(),context = androidContext())  }
}