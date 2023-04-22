package com.codeglo.sampleapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codeglo.sampleapp.R
import com.codeglo.sampleapp.databinding.GridImagelistBinding
import com.codeglo.sampleapp.model.GridImageListValue
import com.codeglo.sampleapp.utils.Utils


class ImageAdapter(var imageList: List<GridImageListValue>) :
    RecyclerView.Adapter<ImageViewHolder>() {

    var listener: OnItemClickListener? = null

    fun ImageAdapter(myClickListener: OnItemClickListener) {
        listener = myClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(isToAdd: Boolean, imageList: String, pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = GridImagelistBinding.inflate(inflater, parent, false)

        return ImageViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        val image = imageList[position].url
        Glide.with(holder.itemView.context).load(image).placeholder(R.drawable.placeholder)
            .into(holder.binding.imageItem)

        holder.binding.imageCheckbox.setOnCheckedChangeListener(null)
        holder.binding.imageCheckbox.isChecked = imageList[position].isChecked

        holder.binding.imageCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            Utils.printLog("${position} + $isChecked")
            this.listener?.onItemClick(isChecked, image, position)
        }

    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}

class ImageViewHolder(val binding: GridImagelistBinding) : RecyclerView.ViewHolder(binding.root) {

}


