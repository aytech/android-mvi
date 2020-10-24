package com.oleg.androidmvi.data

import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.domain.MovieState
import io.reactivex.Observable

interface Interactor {
    fun getMovieList(watched: Boolean): Observable<MovieState>
    fun deleteMovie(movie: Movie): Observable<Unit>
    fun searchMovies(title: String): Observable<MovieState>
    fun addMovie(movie: Movie): Observable<MovieState>
}
