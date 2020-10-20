package com.oleg.androidmvi.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.oleg.androidmvi.R
import com.oleg.androidmvi.action
import com.oleg.androidmvi.data.MovieInteractor
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.domain.MovieState
import com.oleg.androidmvi.presenter.SearchPresenter
import com.oleg.androidmvi.snack
import com.oleg.androidmvi.view.SearchView
import com.oleg.androidmvi.view.adapter.SearchListAdapter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_search_movie.*
import kotlinx.android.synthetic.main.toolbar_view_custom_layout.*
import timber.log.Timber

class SearchMovieActivity : BaseActivity(), SearchView {

    private val toolbar: Toolbar by lazy { toolbar_toolbar_view as Toolbar }
    private val publishSubject: PublishSubject<Movie> = PublishSubject.create()
    private lateinit var presenter: SearchPresenter

    private fun renderFinishState() {
        Timber.d("Render: finish state")
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun renderLoadingState() {
        Timber.d("Render: loading state")
        searchRecyclerView.isEnabled = false
        searchProgressBar.visibility = VISIBLE
    }

    private fun renderConfirmationState(confirmationState: MovieState.ConfirmationState) {
        Timber.d("Render: confirmation state")
        searchLayout.snack("Add ${confirmationState.movie.title} to your list?", LENGTH_LONG) {
            action(getString(R.string.ok)) {
                publishSubject.onNext(confirmationState.movie)
            }
        }
    }

    private fun renderDataState(dataState: MovieState.DataState) {
        Timber.d("Render: data state")
        searchProgressBar.visibility = GONE
        searchRecyclerView.apply {
            isEnabled = true
            (adapter as SearchListAdapter).setMovies(dataState.data)
        }
    }

    private fun renderErrorState(errorState: MovieState.ErrorState) {
        Timber.d("Render: error state")
        searchProgressBar.visibility = GONE
        errorState.error.message?.let { searchLayout.snack(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)
        searchRecyclerView.adapter = SearchListAdapter(emptyList())
        presenter = SearchPresenter(movieInteractor = MovieInteractor())
        presenter.bind(this)
    }

    override fun getToolbarInstance(): Toolbar? = toolbar

    override fun render(state: MovieState) {
        Timber.d("Rendering: $state")
        when (state) {
            MovieState.LoadingState -> renderLoadingState()
            is MovieState.DataState -> renderDataState(state)
            is MovieState.ErrorState -> renderErrorState(state)
            is MovieState.ConfirmationState -> renderConfirmationState(state)
            MovieState.FinishState -> renderFinishState()
        }
    }

    override fun addMovieIntent(): Observable<Movie> =
        (searchRecyclerView.adapter as SearchListAdapter).getViewClickObservable()

    override fun confirmIntent(): Observable<Movie> = publishSubject

    override fun displayMoviesIntent(): Observable<String> =
        Observable.just(intent.extras?.getString("title"))

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

}