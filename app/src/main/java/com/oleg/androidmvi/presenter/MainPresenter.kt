package com.oleg.androidmvi.presenter

import com.oleg.androidmvi.data.MovieInteractor
import com.oleg.androidmvi.domain.MovieState
import com.oleg.androidmvi.view.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainPresenter(private val movieInteractor: MovieInteractor) {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: MainView

    private fun observeMovieDeleteIntent() = view.deleteMovieIntent()
        .doOnNext { Timber.d("Intent: delete movie") }
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(Schedulers.io())
        .flatMap { movieInteractor.deleteMovie(it) }
        .subscribe()

    private fun observeMovieDisplayIntent() = view.displayMoviesIntent()
        .doOnNext { Timber.d("Intent: display movies intent") }
        .flatMap { movieInteractor.getMovieList() }
        .startWith(MovieState.LoadingState)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    fun bind(view: MainView) {
        this.view = view
        compositeDisposable.add(observeMovieDeleteIntent())
        compositeDisposable.add(observeMovieDisplayIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }
}
