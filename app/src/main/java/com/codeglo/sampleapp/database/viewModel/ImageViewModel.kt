package com.codeglo.sampleapp.database.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.codeglo.sampleapp.database.data.ImageDatabase
import com.codeglo.sampleapp.database.model.Image
import com.codeglo.sampleapp.database.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ImageViewModel provides users data to the UI and survive configuration changes.
// A ViewModel acts as a communication center between the Repository and the UI.

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<Image>>
    private val repository: ImageRepository

    init {
        val userDao = ImageDatabase.getDatabase(application).userDao()
        repository = ImageRepository(userDao)
        readAllData = repository.readAllImage
    }

    fun addImage(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addImage(image)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}