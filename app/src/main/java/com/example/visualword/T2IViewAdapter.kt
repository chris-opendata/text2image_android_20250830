package com.example.visualword

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


data class ImageDisplaySize (
    val width: Int,
    val height: Int
)


class T2IViewAdapter(
    private val items: MutableList<TextImageDataItem>,
    private val displaySize: ImageDisplaySize
): RecyclerView.Adapter<T2IViewAdapter.T2IViewHolder>() {
    class T2IViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val itemTextView: TextView = itemView.findViewById(R.id.itemTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T2IViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.text_image_row_item, parent, false)
        return T2IViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: T2IViewHolder, position: Int) {
        val currentItem = items[position]
        holder.itemTextView.text = currentItem.text

        Glide.with(holder.itemView.context)
            .load(currentItem.imageSource)
//            .error(holder.itemTextView.text)
            .override(displaySize.width, displaySize.height)
            .into(holder.itemImageView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItem(position: Int, newItem: TextImageDataItem) {
        if (position in 0 until items.size) {
            items[position] = newItem // Update the data in the list
            notifyItemChanged(position) // Notify that the item was updated at the position
        } else {
            items.add(newItem)
            notifyItemInserted(position)
        }
    }

    fun updateRangeItem(startPosition:Int, endPosition:Int, newItems: List<TextImageDataItem>) {
        if (startPosition in 0 until items.size && endPosition in startPosition until items.size && newItems.size == (endPosition - startPosition +1) ) {
            for((cont, index) in (startPosition..endPosition).withIndex()) {
                items[index] = newItems[cont]
            }
            notifyItemRangeChanged(startPosition,newItems.size) // Notify the adapter
        }
    }
}