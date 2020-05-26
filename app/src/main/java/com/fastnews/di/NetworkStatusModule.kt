package com.fastnews.di

import com.fastnews.mechanism.VerifyNetworkInfo
import org.koin.dsl.module

val verifyNetworkInfoModule = module {
    single { VerifyNetworkInfo(get()) }
}