package com.tailoredapps.codagram.ui.newStoryScreen

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val newStoryScreenModule = module {
    fragment { NewStoryFragment() }
    viewModel { NewStoryViewModel (codaGramApi = get(),context = androidContext()) }
}