package com.codeglo.sampleapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.codeglo.sampleapp.adapter.ImageAdapter
import com.codeglo.sampleapp.database.model.Image
import com.codeglo.sampleapp.databinding.ActivityMainBinding
import com.codeglo.sampleapp.model.GridImageListValue
import com.codeglo.sampleapp.utils.NetworkResult
import com.codeglo.sampleapp.utils.Utils
import com.codeglo.sampleapp.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import com.codeglo.sampleapp.database.viewModel.ImageViewModel as viewModel


/*
 * ADDED BY: DIVYA
 * DATE: 20APR2023
 * PURPOSE: To Create MainActivity
 * DESCRIPTION: To display Images from Api in GridView, Allow the user to select multiple images and continue to next screen
 * VERSION: 1.0
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ImageAdapter.OnItemClickListener {


    lateinit var imageAdapter: ImageAdapter
    private lateinit var binding: ActivityMainBinding
    val imageViewModel by viewModels<ImageViewModel>()

    // var selectedImageList = ArrayList<String>()
    var imageList = mutableListOf<GridImageListValue>()
    var menuClear: MenuItem? = null
    var mImageViewMode: viewModel? = null

    var selectedImageList = mutableListOf<GridImageListValue>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayUseLogoEnabled(true);
        val layoutManager = GridLayoutManager(applicationContext, 3)
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.fitsSystemWindows
        binding.recyclerview.layoutManager = layoutManager

        mImageViewMode = ViewModelProvider(this).get(viewModel::class.java)

        binding.btnContinue.setOnClickListener(View.OnClickListener {

            val list: MutableList<String> = ArrayList()

            for (i in 0 until selectedImageList.size) {
                list.add(selectedImageList.get(i).url)
            }
            val intent = Intent(this@MainActivity, ImageListActivity::class.java)
            intent.putExtra("imageList", list as Serializable)
            startActivity(intent)
        })


    }

    /*  Set Image list in Adapter  */
    fun setDatainAdapter() {
        if (imageList != null && imageList.size > 0) {
            Utils.printLog(imageList.toString())
            imageAdapter = ImageAdapter(imageList)
            // imageAdapter.setImage(imageList)
            imageAdapter.ImageAdapter(this)
            binding.recyclerview.adapter = imageAdapter
            selectedImageList.clear()
            binding.btnContinue.visibility = View.GONE
            showclearAllOption()
        }
    }

    override fun onResume() {
        selectedImageList.clear()
        mImageViewMode?.readAllData?.observe(this, Observer { image ->
            Utils.printLog("${image.size} image size")
            if (image.size > 0) {
                imageList.clear()
                for (i in 0 until image.size) {
                    imageList.add(GridImageListValue(image.get(i).url, image.get(i).isChecked))
                }
                setDatainAdapter()
            } else
                setImageinGrid()

        })

        super.onResume()
    }


    /*  Do internet check and fetch value from url to set in Adapter  */
    fun setImageinGrid() {
        if (Utils.hasInternetConnection(this)) {
            binding.txtNoresponse.visibility = View.GONE
            fetchImage();
        } else if (imageList == null || imageList.size == 0) {
            binding.txtNoresponse.visibility = View.VISIBLE
        } else
            Utils.displayToastMsg(getString(R.string.download_alert), this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {

                setImageinGrid()
            }
            R.id.clear -> {
                for (i in 0 until imageList.size) {
                    imageList.get(i).isChecked = false
                }
                selectedImageList.clear()
                binding.btnContinue.visibility = View.GONE
                imageAdapter.notifyDataSetChanged()
                showclearAllOption()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuClear = menu!!.findItem(R.id.clear)
        var menuDownload = menu!!.findItem(R.id.download)
        menuDownload.isVisible = false
        showclearAllOption()
        return super.onPrepareOptionsMenu(menu)
    }

    /*  Handle Action bar ClearAll Menu visibility based on the item selected */
    fun showclearAllOption() {
        if (menuClear != null) {
            menuClear?.isVisible = false
            menuClear?.isVisible = selectedImageList.size > 0
        }
    }

    /*  To Fetch data from URL and set in adapter */
    private fun fetchImage() {
        imageViewModel.fetchImageResponse()
        binding.progressBar.visibility = View.VISIBLE
        imageViewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    response.data?.let {
                        mImageViewMode?.deleteAll()
                        binding.recyclerview.visibility = View.VISIBLE
                        binding.txtNoresponse.visibility = View.GONE

                        imageList.clear()
                        for (i in 0 until response.data.message.size) {
                            imageList.add(GridImageListValue(response.data.message.get(i), false))
                            mImageViewMode?.addImage(Image(i, response.data.message.get(i), false))
                        }
                        setDatainAdapter()
                    }
                }

                is NetworkResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Utils.printLog("error ")
                    binding.txtNoresponse.visibility = View.VISIBLE
                }

                is NetworkResult.Loading -> {
                    binding.txtNoresponse.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    Utils.printLog("loading ")
                }
            }
        }
    }


    /* Interface from Adapter to handle image selection*/
    override fun onItemClick(isToAdd: Boolean, imageData: String, pos: Int) {

        imageList.set(pos, GridImageListValue(imageData, isToAdd))

        Utils.printLog(imageList.get(pos).url + " onclick ${pos} " + imageList.get(pos).isChecked)
        selectedImageList = imageList.filter { s -> s.isChecked } as MutableList<GridImageListValue>


        Utils.printLog("${selectedImageList.size}")

        if (selectedImageList.size > 2)
            binding.btnContinue.visibility = View.VISIBLE
        else
            binding.btnContinue.visibility = View.GONE

        showclearAllOption()
        binding.recyclerview.post(Runnable {
            imageAdapter.notifyItemChanged(pos)

        })

    }

}