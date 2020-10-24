package com.oleg.androidmvi.data.model

class MovieAction(val movie: Movie, val action: MovieAction.Action) {
    enum class Action { DELETE, ARCHIVE }
}