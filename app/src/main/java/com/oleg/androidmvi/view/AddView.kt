package com.oleg.androidmvi.view

import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.domain.MovieState
import io.reactivex.Observable

interface AddView {
    fun render(state: MovieState)
    fun addMovieIntent(): Observable<Movie>
}
