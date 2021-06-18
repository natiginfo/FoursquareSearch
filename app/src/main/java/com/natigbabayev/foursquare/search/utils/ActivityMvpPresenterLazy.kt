package com.natigbabayev.foursquare.search.utils

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory

/**
 * The purpose of the function is just to have better name when getting the presenter.
 * Uses [ComponentActivity.viewModels] function under the hood.
 */
@MainThread
public inline fun <reified VM : ViewModel> ComponentActivity.mvpPresenters(
    noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> {
    return viewModels(factoryProducer)
}
