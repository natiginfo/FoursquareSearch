package com.natigbabayev.foursquare.search.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.natigbabayev.foursquare.search.R
import com.natigbabayev.foursquare.search.base.MvpActivity
import com.natigbabayev.foursquare.search.databinding.ActivitySearchBinding
import com.natigbabayev.foursquare.search.domain.model.Venue
import com.natigbabayev.foursquare.search.search.venues.VenueAdapter
import com.natigbabayev.foursquare.search.utils.mvpPresenters
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit

private const val REQUEST_CODE = 101

@AndroidEntryPoint
class SearchActivity : MvpActivity<SearchContract.View, SearchPresenter>(), SearchContract.View {

    override val presenter: SearchPresenter by mvpPresenters()

    private lateinit var binding: ActivitySearchBinding
    private val compositeDisposable = CompositeDisposable()

    private lateinit var venueAdapter: VenueAdapter

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        presenter.attachView(this)
        presenter.observeSearchResults()

        startObservingInputChanges()

        setupPermissionRequestButton()
    }

    private fun setupPermissionRequestButton() {
        binding.buttonAllowLocationAccess
            .clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribeBy(
                onNext = {
                    when {
                        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                            onPermissionRequired()
                        }
                        else -> {
                            // You can directly ask for the permission.
                            requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                                REQUEST_CODE
                            )
                        }
                    }
                }
            )
            .addTo(compositeDisposable)
    }

    private fun setupRecyclerView() {
        venueAdapter = VenueAdapter()
        binding.listVenues.run {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = venueAdapter
        }
        venueAdapter.addLoadStateListener { loadState ->

            when (val refresh = loadState.source.refresh) {
                is LoadState.NotLoading -> {
                    if (loadState.append.endOfPaginationReached && venueAdapter.itemCount < 1) {
                        presenter.emptyAdapter()
                    }
                }
                is LoadState.Error -> {
                    presenter.loadingError(refresh.error)
                }
                else -> {
                }
            }
        }
    }

    private fun startObservingInputChanges() {
        binding.inputSearch
            .textChanges()
            .skipInitialValue()
            .filter { it.length >= 3 } // do not trigger search if user hasn't typed at least 3 chars
            .debounce(300, TimeUnit.MILLISECONDS)
            .map { it.toString() }
            .subscribeBy(
                onNext = { presenter.updateSearchKeyword(it) }
            )
            .addTo(compositeDisposable)
    }

    override fun onVenuesLoaded(venues: PagingData<Venue>) {
        with(binding) {
            textMessage.visibility = View.GONE
            buttonAllowLocationAccess.visibility = View.GONE
            listVenues.visibility = View.VISIBLE
        }
        venueAdapter.submitData(lifecycle, venues)
    }

    override fun onSearchWordEmpty() {
        displayMessageAsText(R.string.type_something)
    }

    override fun onVenuesEmpty() {
        displayMessageAsText(R.string.no_results_found)
    }

    override fun onGenericError() {
        binding.buttonAllowLocationAccess.visibility = View.GONE
        displayMessageAsText(R.string.general_error)
    }

    override fun onNetworkError(error: Throwable) {
        Toast.makeText(
            this,
            getString(R.string.loading_error, error.localizedMessage),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPermissionRequired() {
        displayMessageAsText(R.string.permission_required)
        binding.buttonAllowLocationAccess.visibility = View.VISIBLE
    }

    override fun onSearchStarted() {
        displayMessageAsText(R.string.loading)
    }

    private fun displayMessageAsText(@StringRes resId: Int) {
        with(binding) {
            listVenues.visibility = View.GONE
            textMessage.visibility = View.VISIBLE
            textMessage.setText(resId)
        }
    }

    @ExperimentalCoroutinesApi
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    presenter.observeSearchResults()
                    presenter.updateSearchKeyword(binding.inputSearch.text.toString())
                    return
                } else {
                    onPermissionRequired()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
