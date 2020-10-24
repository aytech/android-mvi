package com.oleg.androidmvi.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.oleg.androidmvi.R
import com.oleg.androidmvi.data.MovieInteractor
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.domain.MovieState
import com.oleg.androidmvi.presenter.MainPresenter
import com.oleg.androidmvi.snack
import com.oleg.androidmvi.view.MainView
import com.oleg.androidmvi.view.TabView
import com.oleg.androidmvi.view.adapter.MainViewPagerAdapter
import com.oleg.androidmvi.view.fragment.TabMainWatch
import com.oleg.androidmvi.view.fragment.TabMainWatched
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_view_custom_layout.*
import timber.log.Timber

class MainActivity : BaseActivity(), MainView {

    private var currentTabIndex: Int = 0
    private val tabs = arrayListOf<Fragment>(TabMainWatch(), TabMainWatched())
    private val toolbar: Toolbar by lazy { toolbar_toolbar_view as Toolbar }
    private lateinit var presenter: MainPresenter

    private fun renderLoadingState() {
        Timber.d("Render: loading state")
        progressBar.visibility = VISIBLE
        swipeRefreshMovies.isRefreshing = true
    }

    private fun renderDataState(dataState: MovieState.DataState) {
        Timber.d("Render: data state")
        currentTab.updateData(dataState.data)
        swipeRefreshMovies.isRefreshing = false
        progressBar.visibility = GONE
    }

    private fun renderErrorState(dataState: MovieState.ErrorState) {
        Timber.d("Render: Error State")
        swipeRefreshMovies.isRefreshing = false
        dataState.error.message?.let { mainLayout.snack(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPagerAdapter = MainViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(tabs[0], getString(R.string.watch))
        viewPagerAdapter.addFragment(tabs[1], getString(R.string.watched))
        viewPager.adapter = viewPagerAdapter
        tab_layout_toolbar_view.setupWithViewPager(viewPager)

        tab_layout_toolbar_view.getTabAt(0)?.setIcon(R.drawable.ic_white_movie_24)
        tab_layout_toolbar_view.getTabAt(1)?.setIcon(R.drawable.ic_white_check_24)

        fab.setOnClickListener { startActivity(Intent(this, AddMovieActivity::class.java)) }
        tab_layout_toolbar_view.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabIndex = tab?.position ?: 0
                presenter.bind(this@MainActivity, currentTab.watched)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                presenter.unbind()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Timber.d("Tab reselected")
            }
        })

        presenter = MainPresenter(MovieInteractor())
        presenter.bind(this, currentTab.watched)

        swipeRefreshMovies.setOnRefreshListener { presenter.refresh(currentTab.watched) }
    }

    override fun getToolbarInstance(): Toolbar? = toolbar

    override fun displayMoviesIntent(watched: Boolean): Observable<Unit> = Observable.just(Unit)

    override fun swipeMovieIntent(): Observable<Movie> {
        return currentTab.swipeMovieIntent(this)
    }

    override val currentTab: TabView
        get() = (tabs[currentTabIndex] as TabView)

    override fun render(state: MovieState) {
        when (state) {
            is MovieState.LoadingState -> renderLoadingState()
            is MovieState.DataState -> renderDataState(state)
            is MovieState.ErrorState -> renderErrorState(state)
            is MovieState.ConfirmationState -> TODO()
            MovieState.FinishState -> TODO()
        }
    }

    override fun onStop() {
        presenter.unbind()
        super.onStop()
    }

}