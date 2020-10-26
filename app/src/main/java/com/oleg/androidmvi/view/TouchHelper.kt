package com.oleg.androidmvi.view

interface TouchHelper {
    fun removeMovieAtPosition(position: Int)
    fun handleRightSwipe(position: Int)
}