package com.codeglo.sampleapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*
 * ADDED BY: DIVYA
 * DATE: 20APR2023 TIME: 11:10
 * PURPOSE: To Create MainApplication
 * DESCRIPTION: An application with @HiltAndroidApp that triggers Hilt's code generation and
 * adds an application-level dependency container.
 * VERSION: 1.0
 *
 */
@HiltAndroidApp
class MainApplication : Application() {


}

