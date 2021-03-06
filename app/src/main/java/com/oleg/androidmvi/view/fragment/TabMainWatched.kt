package com.oleg.androidmvi.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.oleg.androidmvi.R
import com.oleg.androidmvi.action
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.data.model.MovieAction
import com.oleg.androidmvi.snack
import com.oleg.androidmvi.view.TabView
import com.oleg.androidmvi.view.TouchHelper
import com.oleg.androidmvi.view.activity.ItemTouchHelperCallback
import com.oleg.androidmvi.view.adapter.MovieListAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.tab_main_watched.*
import kotlinx.android.synthetic.main.tab_main_watched.no_data_text

class TabMainWatched : Fragment(), TabView {

    private var isDeleteUndoActivated: Boolean = false
    private var isRestoreUndoActivated: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_main_watched, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        moviesWatchedRecyclerView.adapter = MovieListAdapter(emptyList())
        moviesWatchedRecyclerView.setOnTouchListener { view, event ->
            when (event.action) {
                ACTION_MOVE -> view.parent.requestDisallowInterceptTouchEvent(true)
                ACTION_UP -> view.performClick()
            }
            false
        }
    }

    override val watched: Boolean
        get() = true

    override fun updateData(data: List<Movie>) {
        if (data.isEmpty()) {
            no_data_text.visibility = VISIBLE
        } else {
            no_data_text.visibility = GONE
            moviesWatchedRecyclerView.apply { (adapter as MovieListAdapter).setMovies(data) }
        }
    }

    override fun swipeMovieIntent(context: Context): Observable<MovieAction> {
        return Observable.create { emitter ->
            val callback = ItemTouchHelperCallback(context, object : TouchHelper {
                override fun removeMovieAtPosition(position: Int) {
                    val adapter = moviesWatchedRecyclerView.adapter as MovieListAdapter
                    val movie = adapter.getMovieAtPosition(position)
                    adapter.removeMovieAtPosition(position)
                    tabWatchedLayout.snack(getString(R.string.movie_removed), LENGTH_LONG, {
                        action(getString(R.string.undo)) {
                            isDeleteUndoActivated = true
                        }
                    }, {
                        if (isDeleteUndoActivated) {
                            adapter.restoreMovieAtPosition(movie, position)
                            isDeleteUndoActivated = false
                        } else {
                            emitter.onNext(MovieAction(movie, MovieAction.Action.DELETE))
                        }
                    })
                }

                override fun handleRightSwipe(position: Int) {
                    val adapter = moviesWatchedRecyclerView.adapter as MovieListAdapter
                    val movie = adapter.getMovieAtPosition(position)
                    adapter.removeMovieAtPosition(position)
                    movie.watched = false
                    tabWatchedLayout.snack(getString(R.string.movie_restored), LENGTH_LONG, {
                        action(getString(R.string.undo)) {
                            isRestoreUndoActivated = true
                        }
                    }, {
                        if (isRestoreUndoActivated) {
                            adapter.restoreMovieAtPosition(movie, position)
                            isRestoreUndoActivated = false
                        } else {
                            emitter.onNext(MovieAction(movie, MovieAction.Action.RESTORE))
                            updateData(adapter.getMovies())
                        }
                    })
                }
            })
            callback.setActionIcon(R.drawable.ic_white_movie_24)
            ItemTouchHelper(callback).attachToRecyclerView(moviesWatchedRecyclerView)
        }
    }
}