package com.codeglo.sampleapp.data.remote

import com.codeglo.sampleapp.api.ApiInterface
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun getImage() =
        apiInterface.getImage()

}