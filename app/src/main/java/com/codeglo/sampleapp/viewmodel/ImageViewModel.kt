package com.codeglo.sampleapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.codeglo.sampleapp.data.Repository
import com.codeglo.sampleapp.model.GridImageValue
import com.codeglo.sampleapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor
    (
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _response: MutableLiveData<NetworkResult<GridImageValue>> = MutableLiveData()
    val response: LiveData<NetworkResult<GridImageValue>> = _response

    fun fetchImageResponse() = viewModelScope.launch {
        repository.getImage().collect { values ->
            _response.value = values
        }
    }
}