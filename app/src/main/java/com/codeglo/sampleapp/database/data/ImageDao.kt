package com.codeglo.sampleapp.database.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.codeglo.sampleapp.database.model.Image

// ImageDao contains the methods used for accessing the database, including queries.
@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // <- Annotate the 'addUser' function below. Set the onConflict strategy to IGNORE so if exactly the same user exists, it will just ignore it.
    suspend fun addImage(image: Image)

    @Query("DELETE FROM image_table")
    suspend fun deleteAll()

    @Query("SELECT * from image_table ORDER BY id ASC") // <- Add a query to fetch all users (in user_table) in ascending order by their IDs.
    fun readAllImage(): LiveData<List<Image>> // <- This means function return type is List. Specifically, a List of Users.
}