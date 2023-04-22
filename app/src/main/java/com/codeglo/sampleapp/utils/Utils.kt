package com.codeglo.sampleapp.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.codeglo.sampleapp.MainActivity
import com.codeglo.sampleapp.R


object Utils {

    /*
     * ADDED BY: DIVYA
     * DATE: 20APR2023
     * PURPOSE: To Check Internet Connection
     * DESCRIPTION: To Check Whether Internet is Connected or Not and return the value in Boolean
     * VERSION: 1.0
     */
    fun hasInternetConnection(context: Context?): Boolean {
        if (context == null)
            return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    /*
    * ADDED BY: DIVYA
    * DATE: 21APR2023
    * PURPOSE: To Display Notification
    * DESCRIPTION: To Display Push Notification in Notification bar and opens MainActivity when click the Push Message
    * VERSION: 1.0
    */
    fun showNotification(body: String?, context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        var pendingIntent: PendingIntent? = null


        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            TaskStackBuilder.create(context) // Add the intent, which inflates the back stack
                .addNextIntent(intent) // Get the PendingIntent containing the entire back stack
                .getPendingIntent(
                    0,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
        } else {
            PendingIntent.getActivity(context, 0, intent, 0)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val NOTIFICATION_CHANNEL_ID = "codeglo_01"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableVibration(true)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)

        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker(context?.getString(R.string.app_name)) //     .setPriority(Notification.PRIORITY_MAX)
            .setContentTitle(context?.getString(R.string.app_name))
            .setContentText(body)
            .setContentIntent(pendingIntent)

        notificationManager?.notify( /*notification id*/1, notificationBuilder.build())

    }

    /*
    * ADDED BY: DIVYA
    * DATE: 21APR2023
    * PURPOSE: To Display Toast Message
    * VERSION: 1.0
    */
    fun displayToastMsg(msg: String?, context: Context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    /*
     * ADDED BY: DIVYA
     * DATE: 21APR2023
     * PURPOSE: To Print Log to test in Debug Case
     * VERSION: 1.0
     */
    fun printLog(msg: String) {
        println("CODEGLO " + msg)
    }



}