package com.oleg.androidmvi.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.oleg.androidmvi.R
import com.oleg.androidmvi.action
import com.oleg.androidmvi.data.MovieInteractor
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.domain.MovieState
import com.oleg.androidmvi.presenter.MainPresenter
import com.oleg.androidmvi.snack
import com.oleg.androidmvi.view.MainView
import com.oleg.androidmvi.view.TouchHelper
import com.oleg.androidmvi.view.adapter.MovieListAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_view_custom_layout.*
import timber.log.Timber

class MainActivity : BaseActivity(), MainView {

    private val toolbar: Toolbar by lazy { toolbar_toolbar_view as Toolbar }
    private lateinit var presenter: MainPresenter

    private fun renderLoadingState() {
        Timber.d("Render: loading state")
        moviesRecyclerView.isEnabled = false
        progressBar.visibility = VISIBLE
    }

    private fun renderDataState(dataState: MovieState.DataState) {
        Timber.d("Render: data state")
        progressBar.visibility = GONE
        moviesRecyclerView.apply {
            isEnabled = true
            (adapter as MovieListAdapter).setMovies(dataState.data)
        }
    }

    private fun renderErrorState(dataState: MovieState.ErrorState) {
        Timber.d("Render: Error State")
        dataState.error.message?.let { mainLayout.snack(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        moviesRecyclerView.adapter = MovieListAdapter(emptyList())
        presenter = MainPresenter(MovieInteractor())
        presenter.bind(this)
        fab.setOnClickListener { startActivity(Intent(this, AddMovieActivity::class.java)) }
        swipeRefreshMovies.setOnRefreshListener {
            moviesRecyclerView.apply {
                (adapter as MovieListAdapter).notifyDataSetChanged()
                swipeRefreshMovies.isRefreshing = false
            }
        }
    }

    override fun getToolbarInstance(): Toolbar? = toolbar

    override fun displayMoviesIntent(): Observable<Unit> = Observable.just(Unit)

    override fun deleteMovieIntent(): Observable<Movie> {
        return Observable.create { emitter ->
            val callback = ItemTouchHelperCallback(this, object : TouchHelper {
                override fun removeMovieAtPosition(position: Int) {
                    val adapter = moviesRecyclerView.adapter as MovieListAdapter
                    val movie = adapter.getMovieAtPosition(position)
                    adapter.removeMovieAtPosition(position)
                    mainLayout.snack(getString(R.string.movie_removed), LENGTH_LONG, {
                        action(getString(R.string.undo)) {
                            adapter.restoreMovieAtPosition(movie, position)
                        }
                    }, {
                        emitter.onNext(movie)
                    })
                }

                override fun markMovieAtPositionAsWatched(position: Int) {
                    val adapter = moviesRecyclerView.adapter as MovieListAdapter
                    val movie = adapter.getMovieAtPosition(position)
                    movie.watched = true
                    
                }
            })
            ItemTouchHelper(callback).attachToRecyclerView(moviesRecyclerView)
        }
    }

    override fun render(state: MovieState) {
        when (state) {
            is MovieState.LoadingState -> renderLoadingState()
            is MovieState.DataState -> renderDataState(state)
            is MovieState.ErrorState -> renderErrorState(state)
            is MovieState.ConfirmationState -> TODO()
            MovieState.FinishState -> TODO()
        }
    }

    override fun onStop() {
        presenter.unbind()
        super.onStop()
    }

}