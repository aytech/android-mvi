package com.oleg.androidmvi.view

import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.domain.MovieState
import io.reactivex.Observable

interface SearchView {
    fun render(state: MovieState)
    fun addMovieIntent(): Observable<Movie>
    fun confirmIntent(): Observable<Movie>
    fun displayMoviesIntent(): Observable<String>
}