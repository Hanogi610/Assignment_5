package com.example.assignment5.core.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val niaDispatcher: MyDispatchers)

enum class MyDispatchers {
    Default,
    IO,
}
