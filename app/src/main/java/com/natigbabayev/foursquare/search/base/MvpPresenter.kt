package com.natigbabayev.foursquare.search.base

import androidx.lifecycle.ViewModel
import io.reactivex.Scheduler
import java.lang.ref.WeakReference

abstract class MvpPresenter<View : MvpView> : ViewModel() {
    private var viewRef: WeakReference<View>? = null

    abstract fun getMainThreadScheduler(): Scheduler

    protected fun ifViewAttached(block: (view: View) -> Unit) {
        getMainThreadScheduler().scheduleDirect {
            val view: View? = viewRef?.get()
            check(view != null) { "No view attached to the presenter" }
            block(view)
        }
    }

    fun attachView(view: View) {
        viewRef?.clear();
        viewRef = WeakReference<View>(view)
    }

    fun detachView() {
        viewRef?.clear();
        viewRef = null
    }

    override fun onCleared() {
        super.onCleared()
        detachView()
    }
}
