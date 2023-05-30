package com.practicum.playlistmaker.di

import android.os.Handler
import android.os.Looper
import org.koin.dsl.module
import java.util.concurrent.Executors

val threadsModule = module {

    factory {
        Executors.newCachedThreadPool()
    }

    factory {
        Handler(Looper.getMainLooper())
    }
}