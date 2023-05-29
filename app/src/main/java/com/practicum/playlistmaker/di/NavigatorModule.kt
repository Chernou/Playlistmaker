package com.practicum.playlistmaker.di

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.utils.NavigationRouter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val navigatorModule = module {

    factory {(activity: ComponentActivity) ->
        NavigationRouter(activity)
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory {(packageContext: Context,
        cls: Class<*>) ->
        Intent(packageContext, cls)
    }
}