package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.data.api.ResourceProvider
import com.practicum.playlistmaker.search.data.impl.ResourceProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val resourceProviderModule = module {

    single<ResourceProvider> {
        ResourceProviderImpl(androidContext())
    }

}