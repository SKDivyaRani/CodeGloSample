package com.codeglo.sampleapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GridImageListValue(var url : String, var isChecked :Boolean):Parcelable


