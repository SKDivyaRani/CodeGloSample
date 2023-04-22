package com.codeglo.sampleapp.data

import com.codeglo.sampleapp.model.ApiResponse
import com.codeglo.sampleapp.model.GridImageValue
import com.codeglo.sampleapp.utils.NetworkResult
import com.codeglo.sampleapp.data.remote.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@ActivityRetainedScoped
class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : ApiResponse() {

    suspend fun getImage(): Flow<NetworkResult<GridImageValue>> {
        return flow<NetworkResult<GridImageValue>> {
            emit(safeApiCall { remoteDataSource.getImage() })
        }.flowOn(Dispatchers.IO)
    }

}
