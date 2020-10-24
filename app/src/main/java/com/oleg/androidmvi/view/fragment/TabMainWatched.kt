package com.oleg.androidmvi.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.oleg.androidmvi.R
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.data.model.MovieAction
import com.oleg.androidmvi.view.TabView
import com.oleg.androidmvi.view.TouchHelper
import com.oleg.androidmvi.view.activity.ItemTouchHelperCallback
import com.oleg.androidmvi.view.adapter.MovieListAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.tab_main_watched.*

class TabMainWatched : Fragment(), TabView {

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
    }

    override val watched: Boolean
        get() = true

    override fun updateData(data: List<Movie>) {
        if (data.isEmpty()) {
            no_data_text.visibility = VISIBLE
        } else {
            moviesWatchedRecyclerView.apply { (adapter as MovieListAdapter).setMovies(data) }
        }
    }

    override fun swipeMovieIntent(context: Context): Observable<MovieAction> {
        return Observable.create { emitter ->
            val callback = ItemTouchHelperCallback(context, object : TouchHelper {
                override fun removeMovieAtPosition(position: Int) {
                    //TODO("Not yet implemented")
                }

                override fun markMovieAtPositionAsWatched(position: Int) {
                    //TODO("Not yet implemented")
                }

            })
            ItemTouchHelper(callback).attachToRecyclerView(moviesWatchedRecyclerView)
        }
    }
}