package com.natigbabayev.foursquare.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class SchedulerModule {
    companion object {
        const val MAIN_SCHEDULER = "MAIN_SCHEDULER"
        const val IO_SCHEDULER = "IO_SCHEDULER"
    }

    @Provides
    @Named(MAIN_SCHEDULER)
    fun provideMainScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @Named(IO_SCHEDULER)
    fun provideIoScheduler(): Scheduler = Schedulers.io()
}
