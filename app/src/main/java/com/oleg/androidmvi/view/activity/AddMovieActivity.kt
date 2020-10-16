package com.oleg.androidmvi.view.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.oleg.androidmvi.R
import com.oleg.androidmvi.action
import com.oleg.androidmvi.data.MovieInteractor
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.domain.MovieState
import com.oleg.androidmvi.presenter.AddPresenter
import com.oleg.androidmvi.snack
import com.oleg.androidmvi.view.AddView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_add_movie.*
import kotlinx.android.synthetic.main.toolbar_view_custom_layout.*
import timber.log.Timber

class AddMovieActivity : BaseActivity(), AddView {

    private val toolbar: Toolbar by lazy { toolbar_toolbar_view as Toolbar }
    private val publishSubject: PublishSubject<Movie> = PublishSubject.create()
    private lateinit var presenter: AddPresenter

    private fun renderFinishState() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showMessage(message: String) {
        addLayout.snack(message, LENGTH_INDEFINITE) {
            action(getString(R.string.ok)) {}
        }
    }

    private fun goToSearchMovieActivity() {
        if (titleEditText.text.toString().isNotBlank()) {
            Timber.d("Navigating to Search activity")
        } else {
            showMessage(getString(R.string.must_enter_title))
        }
    }

    private fun addMovieClick() {
        if (titleEditText.text.toString().isNotBlank()) {
            publishSubject.onNext(
                Movie(
                    title = titleEditText.text.toString(),
                    releaseDate = yearEditText.text.toString()
                )
            )
        } else {
            showMessage(getString(R.string.must_enter_title))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)
        presenter = AddPresenter(MovieInteractor())
        presenter.bind(this)
        addMovieButton.setOnClickListener { addMovieClick() }
        searchButton.setOnClickListener { goToSearchMovieActivity() }
    }

    override fun getToolbarInstance(): Toolbar? = toolbar

    override fun render(state: MovieState) {
        when (state) {
            is MovieState.FinishState -> renderFinishState()
            else -> Timber.d("Not implemented")
        }
    }

    override fun addMovieIntent(): Observable<Movie> = publishSubject

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

}