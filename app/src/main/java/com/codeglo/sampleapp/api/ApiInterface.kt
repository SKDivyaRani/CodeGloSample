package com.codeglo.sampleapp.api

import com.codeglo.sampleapp.model.GridImageValue
import com.codeglo.sampleapp.utils.Constants
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET(Constants.IMAGE_URL)
    suspend fun getImage(): Response<GridImageValue>
}
