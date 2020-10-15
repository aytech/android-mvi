package com.oleg.androidmvi.view

import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.domain.MovieState
import io.reactivex.Observable

interface MainView {
    fun render(state: MovieState)
    fun deleteMovieIntent(): Observable<Movie>
    fun displayMoviesIntent(): Observable<Unit>
}