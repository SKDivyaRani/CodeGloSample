package com.codeglo.sampleapp.database.repository

import androidx.lifecycle.LiveData
import com.codeglo.sampleapp.database.data.ImageDao
import com.codeglo.sampleapp.database.model.Image

// ImageRepository abstracts access to multiple data sources. However this is not the part of the Architecture Component libraries.

class ImageRepository(private val userDao: ImageDao) {
    val readAllImage: LiveData<List<Image>> = userDao.readAllImage()

    suspend fun addImage(image: Image) {
        userDao.addImage(image)
    }

    suspend fun deleteAll() {
        userDao.deleteAll()
    }
}