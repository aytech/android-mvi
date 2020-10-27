package com.oleg.androidmvi.data.model

class MovieAction(val movie: Movie, val action: Action) {
    enum class Action { DELETE, ARCHIVE, RESTORE }
}