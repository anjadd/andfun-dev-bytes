package com.example.android.devbyteviewer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.devbyteviewer.databinding.DevbyteItemBinding
import com.example.android.devbyteviewer.domain.Video

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class DevByteAdapter(private val clickListener: VideoClick) : ListAdapter<Video, DevByteViewHolder>(VideoDiffCallback) {

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevByteViewHolder {
        return DevByteViewHolder.from(parent)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: DevByteViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    companion object VideoDiffCallback : DiffUtil.ItemCallback<Video>() {

        /* For the areItemsTheSame() method, use Kotlin's referential equality operator (===),
        which returns true if the object references for oldItem and newItem are the same. */
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem === newItem
        }

        /* For the areContentsTheSame() method, use the standard structural equality operator on
         just the ID of oldItem and newItem. */
        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * ViewHolder for DevByte items. All work is done by data binding.
 */
class DevByteViewHolder(private val viewDataBinding: DevbyteItemBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {

        fun from(parent: ViewGroup): DevByteViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DevbyteItemBinding.inflate(layoutInflater, parent, false)
            return DevByteViewHolder(binding)
        }
    }

    fun bind(item: Video, clickListener: VideoClick) {
        viewDataBinding.video = item
        viewDataBinding.videoClickListener = clickListener
        viewDataBinding.executePendingBindings()
    }
}

/**
 * Click listener for Videos. By giving the block a name (clickListener),
 * it helps a reader understand what it does.
 */
class VideoClick(val clickListener: (Video) -> Unit) {
    /**
     * Called when a video is clicked
     *
     * @param video the video that was clicked
     */
    fun onClick(video: Video) = clickListener(video)
}