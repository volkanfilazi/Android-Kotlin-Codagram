 package com.tailoredapps.codagram.ui.groupscreen

import com.tailoredapps.codagram.ui.loginscreen.LoginFragment
import com.tailoredapps.codagram.ui.loginscreen.LoginViewModel
import com.tailoredapps.codagram.ui.newStoryScreen.NewStoryFragment
import com.tailoredapps.codagram.ui.newStoryScreen.NewStoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
internal val groupScreenModule = module {
    factory { SearchAdapter() }
    factory { GroupDetailsAdapter() }
    factory { GroupInviteAdapter() }
    fragment { GroupFragment() }
    fragment { GroupDetailsFragment()}
    viewModel { GroupViewModel (codaGramApi = get(), context = androidContext()) }
    viewModel { GroupDetailsViewModel (codaGramApi = get(), context = androidContext()) }
    factory { GroupAdapter() }
    fragment { MyGroupScreen() }
    viewModel { MyGroupScreenViewMode(codaGramApi = get(), context = androidContext()) }
}