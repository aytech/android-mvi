package com.oleg.androidmvi.presenter

import com.oleg.androidmvi.data.MovieInteractor
import com.oleg.androidmvi.data.model.MovieAction
import com.oleg.androidmvi.domain.MovieState
import com.oleg.androidmvi.view.MainView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainPresenter(private val movieInteractor: MovieInteractor) {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: MainView

    private fun observeMovieSwipeIntent() = view.swipeMovieIntent()
        .doOnNext { Timber.d("Intent: delete movie") }
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(Schedulers.io())
        .flatMap { swipeMovie(it) }
        .doOnError { Timber.d("Error: $it") }
        .subscribe()

    private fun swipeMovie(movieAction: MovieAction): Observable<Unit> {
        return when (movieAction.action) {
            MovieAction.Action.DELETE -> TODO()
            MovieAction.Action.ARCHIVE,
            MovieAction.Action.RESTORE -> movieInteractor.updateMovie(movieAction.movie)
        }
    }

    private fun observeMovieDisplayIntent(getWatched: Boolean) =
        view.displayMoviesIntent(getWatched)
            .doOnNext { Timber.d("Intent: display movies intent") }
            .flatMap { movieInteractor.getMovieList(getWatched) }
            .startWith(MovieState.LoadingState)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { Timber.d("Error: $it") }
            .subscribe { view.render(it) }

    fun bind(view: MainView) {
        this.view = view
    }

    fun activate(getWatched: Boolean) {
        compositeDisposable.add(observeMovieSwipeIntent())
        compositeDisposable.add(observeMovieDisplayIntent(getWatched))
    }

    fun deactivate() {
        compositeDisposable.clear()
    }

    fun refresh(getWatched: Boolean) {
        deactivate()
        activate(getWatched)
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

}
