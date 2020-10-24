package com.oleg.androidmvi.view

import com.oleg.androidmvi.data.model.MovieAction
import com.oleg.androidmvi.domain.MovieState
import io.reactivex.Observable

interface MainView {
    val currentTab: TabView
    fun render(state: MovieState)
    fun swipeMovieIntent(): Observable<MovieAction>
    fun displayMoviesIntent(watched: Boolean): Observable<Unit>
}