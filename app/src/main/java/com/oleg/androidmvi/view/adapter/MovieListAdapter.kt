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
import kotlinx.android.synthetic.main.item_movie_main.view.*
import timber.log.Timber

class MovieListAdapter(private var movies: List<Movie>) :
    RecyclerView.Adapter<MovieListAdapter.MovieHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie_main, parent, false)
        return MovieHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieHolder, position: Int) =
        holder.bind(movie = movies[position])

    fun setMovies(movies: List<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    fun getMovieAtPosition(position: Int) = movies[position]

    fun removeMovieAtPosition(position: Int) {
        val movies = ArrayList(this.movies)
        movies.remove(this.movies[position])
        setMovies(movies)
    }

    fun restoreMovieAtPosition(movie: Movie, position: Int) {
        val movies = ArrayList(this.movies)
        movies.add(position, movie)
        setMovies(movies)
    }

    inner class MovieHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(movie: Movie) = with(view) {
            movieTitleTextView.text = movie.title
            movieReleaseDateTextView.text = movie.releaseDate
            if (movie.posterPath != null) {
                Picasso.get().load(TMDB_IMAGE_URL + movie.posterPath).into(movieImageView)
            } else {
                movieImageView.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_local_movies_gray,
                        null
                    )
                )
            }
        }
    }

}
