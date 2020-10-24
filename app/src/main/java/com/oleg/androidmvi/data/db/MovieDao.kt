package com.oleg.androidmvi.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.oleg.androidmvi.data.model.Movie
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface MovieDao {

    @Insert(onConflict = REPLACE)
    fun insert(movie: Movie): Single<Long>

    @Query("select * from movie")
    fun getAll(): Observable<List<Movie>>

    @Query("select * from movie where watched = :watched")
    fun get(watched: Boolean): Observable<List<Movie>>

    @Delete
    fun delete(movie: Movie): Completable

}
