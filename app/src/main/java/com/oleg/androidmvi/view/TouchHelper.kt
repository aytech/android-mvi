package com.oleg.androidmvi.view

interface TouchHelper {
    fun removeMovieAtPosition(position: Int)
    fun markMovieAtPositionAsWatched(position: Int)
}