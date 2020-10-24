package com.oleg.androidmvi.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.oleg.androidmvi.R
import com.oleg.androidmvi.action
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.presenter.MainPresenter
import com.oleg.androidmvi.snack
import com.oleg.androidmvi.view.TabView
import com.oleg.androidmvi.view.TouchHelper
import com.oleg.androidmvi.view.activity.ItemTouchHelperCallback
import com.oleg.androidmvi.view.adapter.MovieListAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.tab_main_watch.*

class TabMainWatch(private val presenter: MainPresenter) : Fragment(), TabView {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_main_watch, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        moviesWatchRecyclerView.adapter = MovieListAdapter(emptyList())
        presenter.activate(watched)
    }

    override val watched: Boolean
        get() = false

    override fun updateData(data: List<Movie>) {
        if (data.isEmpty()) {
            no_data_text.visibility = VISIBLE
        } else {
            moviesWatchRecyclerView.apply { (adapter as MovieListAdapter).setMovies(data) }
        }
    }

    override fun swipeMovieIntent(context: Context): Observable<Movie> {
        return Observable.create { emitter ->
            val callback = ItemTouchHelperCallback(context, object : TouchHelper {
                override fun removeMovieAtPosition(position: Int) {
                    val adapter = moviesWatchRecyclerView.adapter as MovieListAdapter
                    val movie = adapter.getMovieAtPosition(position)
                    adapter.removeMovieAtPosition(position)
                    tabWatchedLayout.snack(getString(R.string.movie_removed), LENGTH_LONG, {
                        action(getString(R.string.undo)) {
                            adapter.restoreMovieAtPosition(movie, position)
                        }
                    }, {
                        // emitter.onNext(movie)
                    })
                }

                override fun markMovieAtPositionAsWatched(position: Int) {
                    val adapter = moviesWatchRecyclerView.adapter as MovieListAdapter
                    val movie = adapter.getMovieAtPosition(position)
                    movie.watched = true
                }
            })
            ItemTouchHelper(callback).attachToRecyclerView(moviesWatchRecyclerView)
        }
    }
}