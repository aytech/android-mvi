package com.oleg.androidmvi.data

import com.oleg.androidmvi.App
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.data.net.RetrofitClient
import com.oleg.androidmvi.domain.MovieState
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class MovieInteractor : Interactor {

    private val retrofitClient = RetrofitClient()
    private val movieDao = App.INSTANCE.db.movieDao()

    override fun getMovieList(watched: Boolean): Observable<MovieState> {
        return movieDao.get(watched)
            .map<MovieState> { MovieState.DataState(it) }
            .onErrorReturn { MovieState.ErrorState(it) }
    }

    override fun deleteMovie(movie: Movie): Observable<Unit> = movieDao.delete(movie).toObservable()

    override fun updateMovie(movie: Movie): Observable<Unit> = movieDao.update(movie).toObservable()

    override fun searchMovies(title: String): Observable<MovieState> =
        retrofitClient.searchMovies(title).observeOn(Schedulers.io())
            .map<MovieState> { it -> it.results?.let { MovieState.DataState(it) } }
            .onErrorReturn { MovieState.ErrorState(it) }

    override fun addMovie(movie: Movie): Observable<MovieState> =
        movieDao.insert(movie = movie).map<MovieState> { MovieState.FinishState }.toObservable()
}