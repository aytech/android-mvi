package com.oleg.androidmvi.presenter

import com.oleg.androidmvi.data.MovieInteractor
import com.oleg.androidmvi.view.AddView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddPresenter(private val movieInteractor: MovieInteractor) {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var view: AddView

    private fun observeAddMovieIntent(): Disposable =
        view.addMovieIntent()
            .observeOn(Schedulers.io())
            .flatMap { movieInteractor.addMovie(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.render(it) }

    fun bind(view: AddView) {
        this.view = view
        compositeDisposable.add(observeAddMovieIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

}
