package com.oleg.androidmvi.view

import android.content.Context
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.data.model.MovieAction
import io.reactivex.Observable

interface TabView {

    val watched: Boolean
    fun swipeMovieIntent(context: Context): Observable<MovieAction>
    fun updateData(data: List<Movie>)

}