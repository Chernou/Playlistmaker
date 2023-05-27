package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.utils.ResourceProvider
import com.practicum.playlistmaker.utils.ResourceProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val resourceProviderModule = module {

    single<ResourceProvider> {
        ResourceProviderImpl(androidContext())
    }

}