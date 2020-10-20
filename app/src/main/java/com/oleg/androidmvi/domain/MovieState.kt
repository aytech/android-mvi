package com.oleg.androidmvi.domain

import com.oleg.androidmvi.data.model.Movie

sealed class MovieState {
    object LoadingState : MovieState()
    data class DataState(val data: List<Movie>) : MovieState()
    data class ErrorState(val error: Throwable) : MovieState()
    data class ConfirmationState(val movie: Movie) : MovieState()
    object FinishState : MovieState()
}
