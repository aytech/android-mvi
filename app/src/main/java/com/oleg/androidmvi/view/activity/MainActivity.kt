package com.oleg.androidmvi.view.activity

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.oleg.androidmvi.R
import com.oleg.androidmvi.data.MovieInteractor
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.domain.MovieState
import com.oleg.androidmvi.presenter.MainPresenter
import com.oleg.androidmvi.snack
import com.oleg.androidmvi.view.MainView
import com.oleg.androidmvi.view.adapter.MovieListAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_view_custom_layout.*
import timber.log.Timber

class MainActivity : BaseActivity(), MainView {

    private val toolbar: Toolbar by lazy { toolbar_toolbar_view as Toolbar }
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        moviesRecyclerView.adapter = MovieListAdapter(emptyList())
        presenter = MainPresenter(MovieInteractor())
        presenter.bind(this)
    }

    override fun getToolbarInstance(): Toolbar? = toolbar

    override fun displayMoviesIntent(): Observable<Unit> = Observable.just(Unit)

    override fun deleteMovieIntent(): Observable<Movie> {
        return Observable.create { emitter ->
            // Add the functionality to swipe items in the
            // recycler view to delete that item
            val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {
                // Not implemented
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.absoluteAdapterPosition
                    val movie =
                        (moviesRecyclerView.adapter as MovieListAdapter).getMovieAtPosition(position)
                    emitter.onNext(movie)
                }
            })
            helper.attachToRecyclerView(moviesRecyclerView)
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
        mainLayout.snack(dataState.data)
    }

    override fun onStop() {
        presenter.unbind()
        super.onStop()
    }
}