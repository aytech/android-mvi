package com.oleg.androidmvi.view.activity

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.oleg.androidmvi.R
import com.oleg.androidmvi.action
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
    }

    override fun getToolbarInstance(): Toolbar? = toolbar

    override fun displayMoviesIntent(): Observable<Unit> = Observable.just(Unit)

    override fun deleteMovieIntent(): Observable<Movie> {
        return Observable.create { emitter ->
            // Add the functionality to swipe items in the
            // recycler view to delete that item
            val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {
                val iconDelete =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_white_delete_sweep_24
                    )
                val iconMarkRead =
                    ContextCompat.getDrawable(applicationContext, R.drawable.ic_white_check_24)
                var background = ColorDrawable(RED)

                // Not implemented
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )

                    val iconDeleteHeight = iconDelete?.intrinsicHeight ?: 0
                    val iconMarkReadHeight = iconMarkRead?.intrinsicHeight ?: 0
                    val iconDeleteWidth = iconDelete?.intrinsicWidth ?: 0
                    val iconMarkReadWidth = iconMarkRead?.intrinsicWidth ?: 0
                    val itemView: View = viewHolder.itemView
                    val backgroundCornerOffset =
                        20 //so background is behind the rounded corners of itemView
                    val iconDeleteMargin: Int = (itemView.height - iconDeleteHeight) / 3
                    val iconMarkReadMargin: Int = (itemView.height - iconMarkReadHeight) / 3
                    val iconDeleteTop: Int = itemView.top + (itemView.height - iconDeleteHeight) / 2
                    val iconMarkReadTop: Int =
                        itemView.top + (itemView.height - iconMarkReadHeight) / 2
                    val iconDeleteBottom: Int = iconDeleteTop + iconDeleteHeight
                    val iconMarkReadBottom: Int = iconMarkReadTop + iconMarkReadHeight

                    when {
                        dX > 0 -> { // Swiping right, add to constructor to support
                            val iconLeft: Int =
                                itemView.left + iconMarkReadMargin
                            val iconRight: Int =
                                itemView.left + iconMarkReadMargin + iconMarkReadWidth
                            Timber.d("Left: %s, right: %s", iconLeft, iconRight)
                            iconMarkRead?.setBounds(
                                iconLeft,
                                iconMarkReadTop,
                                iconRight,
                                iconMarkReadBottom
                            )
                            background = ColorDrawable(MAGENTA)
                            background.setBounds(
                                itemView.left,
                                itemView.top,
                                itemView.left + dX.toInt() + backgroundCornerOffset,
                                itemView.bottom
                            )
                        }
                        dX < 0 -> { // Swiping left
                            val iconLeft: Int = itemView.right - iconDeleteMargin - iconDeleteWidth
                            val iconRight: Int = itemView.right - iconDeleteMargin
                            Timber.d("Left: %s, right: %s", iconLeft, iconRight)
                            iconDelete?.setBounds(
                                iconLeft,
                                iconDeleteTop,
                                iconRight,
                                iconDeleteBottom
                            )
                            background = ColorDrawable(RED)
                            background.setBounds(
                                itemView.right + dX.toInt() - backgroundCornerOffset,
                                itemView.top,
                                itemView.right,
                                itemView.bottom
                            )

                        }
                        else -> { // no swipe happened yet
                            background.setBounds(0, 0, 0, 0)
                        }
                    }

                    background.draw(c)
                    iconDelete?.draw(c)
                    iconMarkRead?.draw(c)
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.absoluteAdapterPosition
                    val adapter: MovieListAdapter = moviesRecyclerView.adapter as MovieListAdapter
                    val movie: Movie = adapter.getMovieAtPosition(position)
                    adapter.removeMovieAtPosition(position)
                    mainLayout.snack(getString(R.string.movie_removed), LENGTH_LONG, {
                        action(getString(R.string.undo)) {
                            adapter.restoreMovieAtPosition(movie, position)
                        }
                    }, {
                        //emitter.onNext(movie)
                    })
                }

                override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.7f
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

    override fun onStop() {
        presenter.unbind()
        super.onStop()
    }

}