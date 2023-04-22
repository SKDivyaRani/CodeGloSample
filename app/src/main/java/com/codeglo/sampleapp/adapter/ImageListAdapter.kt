package com.codeglo.sampleapp.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codeglo.sampleapp.R
import com.codeglo.sampleapp.databinding.GridImageBinding

public class ImageListAdapter(private val imageList: ArrayList<String>) :
    RecyclerView.Adapter<ImageListViewHolder>() {


    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(imageList: String)
    }

    fun ImageListAdapter(myClickListener: OnItemClickListener) {
        listener = myClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = GridImageBinding.inflate(inflater, parent, false)
        return ImageListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        val image = imageList[position]
        Glide.with(holder.itemView.context).load(image).placeholder(R.drawable.placeholder)
            .into(holder.binding.imageItem)
        holder.binding.btnDownload.setOnClickListener(View.OnClickListener {
            this.listener?.onItemClick(image)
        })
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}

class ImageListViewHolder(val binding: GridImageBinding) : RecyclerView.ViewHolder(binding.root) {

}