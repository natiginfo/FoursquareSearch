package com.natigbabayev.foursquare.search.di

import com.natigbabayev.foursquare.search.data.LocationRepositoryImpl
import com.natigbabayev.foursquare.search.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class LocationRepositoryModule {

    @Binds
    abstract fun bindRepository(impl: LocationRepositoryImpl): LocationRepository
}
