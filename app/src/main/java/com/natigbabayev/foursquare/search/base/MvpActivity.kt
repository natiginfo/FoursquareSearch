package com.natigbabayev.foursquare.search.base

import androidx.appcompat.app.AppCompatActivity

abstract class MvpActivity<View : MvpView, Presenter : MvpPresenter<View>> : AppCompatActivity() {

    abstract val presenter: Presenter

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
