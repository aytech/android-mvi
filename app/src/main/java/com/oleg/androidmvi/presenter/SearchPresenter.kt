package com.oleg.androidmvi.presenter

import com.oleg.androidmvi.data.MovieInteractor
import com.oleg.androidmvi.domain.MovieState
import com.oleg.androidmvi.view.SearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchPresenter(private val movieInteractor: MovieInteractor) {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var searchView: SearchView

    private fun observeConfirmationIntent() = searchView.confirmIntent()
        .doOnNext { Timber.d("Intent: confirm") }
        .observeOn(Schedulers.io())
        .flatMap { movieInteractor.addMovie(it) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { searchView.render(it) }

    private fun observeAddMovieIntent() = searchView.addMovieIntent()
        .doOnNext { Timber.d("Intent: add movie") }
        .map { MovieState.ConfirmationState(it) }
        .subscribe { searchView.render(it) }

    private fun observeMovieDisplayIntent() = searchView.displayMoviesIntent()
        .doOnNext { Timber.d("Intent: display movies") }
        .flatMap { movieInteractor.searchMovies(it) }
        .startWith(MovieState.LoadingState)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { searchView.render(it) }

    fun bind(searchView: SearchView) {
        this.searchView = searchView
        compositeDisposable.add(observeMovieDisplayIntent())
        compositeDisposable.add(observeAddMovieIntent())
        compositeDisposable.add(observeConfirmationIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

}
