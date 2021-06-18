package com.natigbabayev.foursquare.search.di

import com.natigbabayev.foursquare.search.data.VenueRepositoryImpl
import com.natigbabayev.foursquare.search.domain.repository.VenueRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class VenueRepositoryModule {

    @Binds
    abstract fun bindVenueRepository(impl: VenueRepositoryImpl): VenueRepository
}
