package com.oleg.androidmvi.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.oleg.androidmvi.R
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.view.adapter.MovieListAdapter
import kotlinx.android.synthetic.main.tab_main_watch.*
import timber.log.Timber

class TabMainWatch : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_main_watch, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        moviesRecyclerView.adapter = MovieListAdapter(emptyList())
    }

    fun updateData(movies: List<Movie>) {
        moviesRecyclerView.apply {
            isEnabled = true
            (adapter as MovieListAdapter).setMovies(movies)
        }
    }

}