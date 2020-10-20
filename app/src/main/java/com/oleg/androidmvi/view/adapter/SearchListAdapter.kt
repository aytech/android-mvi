package com.oleg.androidmvi.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.oleg.androidmvi.R
import com.oleg.androidmvi.data.model.Movie
import com.oleg.androidmvi.data.net.RetrofitClient.Companion.TMDB_IMAGE_URL
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_movie_search.view.*

class SearchListAdapter(private var movies: List<Movie>) :
    RecyclerView.Adapter<SearchListAdapter.MovieHolder>() {

    val publishSubject: PublishSubject<Movie> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie_search, parent, false)
        return MovieHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieHolder, position: Int) =
        holder.bind(movie = movies[position], position)

    fun setMovies(movies: List<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    fun getViewClickObservable(): Observable<Movie> = publishSubject

    inner class MovieHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(movie: Movie, position: Int) = with(view) {
            searchTitleTextView.text = movie.title
            searchReleaseDateTextView.text = movie.releaseDate
            view.setOnClickListener { publishSubject.onNext(movies[position]) }
            if (movie.posterPath == null) {
                searchImageView.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_local_movies_gray,
                        null
                    )
                )
            } else {
                Picasso.get().load(TMDB_IMAGE_URL + movie.posterPath).into(searchImageView)
            }
        }
    }
}
