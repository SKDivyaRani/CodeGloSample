package com.codeglo.sampleapp

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.codeglo.sampleapp.adapter.ImageListAdapter
import com.codeglo.sampleapp.databinding.ActivityImagelistBinding
import com.codeglo.sampleapp.utils.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/*
 * ADDED BY: DIVYA
 * DATE: 21APR2023
 * PURPOSE: To Create ImageListActivity
 * DESCRIPTION: To display Images selected by the user from MainActivity and allow user to download the image
 * VERSION: 1.0
 */

class ImageListActivity : AppCompatActivity(), ImageListAdapter.OnItemClickListener {

    var imageList = ArrayList<String>()
    var imageListToDownload = ArrayList<String>()
    private lateinit var binding: ActivityImagelistBinding
    val REQUEST_WRITE_PERMISSION: Int = 786
    val REQUEST_NOTIFICATION_PERMISSION: Int = 787

    var count: Int = 0;


    // var selectedImageList = mutableListOf<GridImageListValue>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagelistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        val layoutManager = GridLayoutManager(applicationContext, 3)
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.fitsSystemWindows
        binding.recyclerview.layoutManager = layoutManager

        var selectedImageList = intent.getStringArrayListExtra("imageList")
        if (selectedImageList != null) {
            imageList = selectedImageList
        }

        Utils.printLog(imageList.toString())

        setImageinList()
    }

    /*  Do internet check and set selected image in Adapter  */
    fun setImageinList() {
        if (Utils.hasInternetConnection(this) && imageList != null && imageList.size > 0) {
            var imageListAdapter = ImageListAdapter(imageList)
            binding.recyclerview.visibility = View.VISIBLE
            binding.txtNoresponse.visibility = View.GONE
            imageListAdapter.ImageListAdapter(this)
            binding.recyclerview.adapter = imageListAdapter
        } else {
            binding.txtNoresponse.visibility = View.VISIBLE
            binding.recyclerview.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> setImageinList()
            android.R.id.home -> finish()
            R.id.download -> {
                imageListToDownload.clear()
                imageListToDownload.addAll(imageList)
                checkPermissions()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        var menuClear = menu!!.findItem(R.id.clear)
        menuClear.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    /*  DownloadManager to download image from url in Notification bar  */
    fun downloadFile(uRl: String, n: Int) {
        val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss")
        val timeStamp: String = dateFormat.format(Date())
        val imageFileName = "$timeStamp.png"
        val myDir =
            File(Environment.getExternalStorageDirectory(), getString(R.string.txt_appname) + "/")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val mgr = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri: Uri = Uri.parse(uRl)
        val request = DownloadManager.Request(
            downloadUri
        )
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI
                    or DownloadManager.Request.NETWORK_MOBILE
        ).setAllowedOverMetered(true)
            .setAllowedOverRoaming(true).setTitle(getString(R.string.download_msg, n))
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                getString(R.string.txt_appname) + "/" + imageFileName
            )
        mgr.enqueue(request)

        Utils.printLog(Environment.DIRECTORY_PICTURES + imageFileName)

    }

    /* BroadCastReceiver to display Notification once the image download get completed */
    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            count++

            Utils.printLog("${imageListToDownload.size} + $count")
            if (count == imageListToDownload.size) {
                Utils.showNotification(
                    this@ImageListActivity.getString(R.string.txt_download_completed),
                    this@ImageListActivity
                )
            }

        }
    }

    /*  Check POST_NOTIFICATION permission in Android 13 and EXTERNAL_STORAGE permission below Android 13 */
    fun checkPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            val permissionNotification = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionNotification != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            } else {
                downloadImage()
            }
        } else {
            val permissionRead = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val permissionWrite = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            if (permissionRead != PackageManager.PERMISSION_GRANTED || permissionWrite != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_WRITE_PERMISSION
                )
            } else {
                downloadImage()
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_PERMISSION -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED
                    || grantResults[1] != PackageManager.PERMISSION_GRANTED
                ) {
                    Utils.displayToastMsg(getString(R.string.network_alert), this)
                } else {
                    downloadImage()
                }
            }
            REQUEST_NOTIFICATION_PERMISSION -> {
                downloadImage()
            }
        }
    }

    fun downloadImage() {

        if (imageListToDownload != null) {
            count = 0
            registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            Utils.displayToastMsg(getString(R.string.txt_download_alert), this)
            for (i in 0 until imageListToDownload.size) {
                if (Utils.hasInternetConnection(this)) {
                    downloadFile(imageListToDownload.get(i), i + 1)
                } else Utils.displayToastMsg(getString(R.string.network_alert), this)
            }
        }
    }

    override fun onDestroy() {
        try {
            if (receiver != null)
                unregisterReceiver(receiver);
        } catch (e: java.lang.Exception) {

        }
        super.onDestroy()
    }

    override fun onItemClick(imageItem: String) {
        imageListToDownload.clear()
        imageListToDownload.add(imageItem)
        Utils.printLog("onitemclick ${imageListToDownload.size}")
        checkPermissions()
    }


}