package com.tailoredapps.codagram.ui.homeFeedScreen

import com.tailoredapps.codagram.ui.HomeFeedScreen
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val firstViewModule = module {
    factory { FilterGroupAdapter() }
    factory { HomeFeedAdapter(codaGramApi = get(),context = androidContext()) }
    factory { CommentScreenAdapter(codaGramApi = get ()) }
    fragment { CommentScreenFragment() }
    fragment { HomeFeedScreen() }
    viewModel { HomeFeedViewModel(codaGramApi = get(),context = androidContext()) }
    viewModel { CommentScreenViewModel(codaGramApi = get(),context = androidContext()) }
}
