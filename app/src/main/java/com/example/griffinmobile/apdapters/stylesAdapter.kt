package com.example.griffinmobile.apdapters

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.griffinmobile.R

class stylesAdapter(
    private val imageResList: List<Int>?,
    private val onItemClick: (resourceName: String) -> Unit // تعریف تابع به جای اینترفیس
) : RecyclerView.Adapter<stylesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.style_imageview)

        fun bind(imageResId: Int) {
            imageView.setImageResource(imageResId)
            val resourceName = try {
                itemView.resources.getResourceEntryName(imageResId)
            } catch (e: Resources.NotFoundException) {
                "unknown_resource"
            }
            itemView.setOnClickListener { onItemClick(resourceName) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.styles_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        imageResList?.get(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = imageResList?.size ?: 0
}