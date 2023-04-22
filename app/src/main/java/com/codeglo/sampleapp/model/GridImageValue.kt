package com.codeglo.sampleapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class GridImageValue(

    @SerializedName("message")
     val message: ArrayList<String>


): Parcelable

