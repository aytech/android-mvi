package com.oleg.androidmvi.view

import android.content.Context
import com.oleg.androidmvi.data.model.Movie
import io.reactivex.Observable

interface TabView {

    val watched: Boolean
    fun updateData(data: List<Movie>)
    fun swipeMovieIntent(context: Context): Observable<Movie>

}