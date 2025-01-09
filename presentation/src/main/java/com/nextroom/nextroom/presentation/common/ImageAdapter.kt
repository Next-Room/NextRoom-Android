package com.nextroom.nextroom.presentation.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nextroom.nextroom.presentation.databinding.ItemImageBinding

class ImageAdapter(
    private val onImageClicked: (() -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val images = mutableListOf<Image>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemImageBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
            .let { ImageViewHolder(it, onImageClicked) }
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ImageViewHolder)?.bind(images[position])
    }

    fun setList(items: List<Image>) {
        this.images.clear()
        this.images.addAll(items)
        notifyDataSetChanged()
    }

    class ImageViewHolder(
        private val binding: ItemImageBinding,
        private val onImageClicked: (() -> Unit)? = null
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            when (image) {
                is Image.Drawable -> binding.ivError.setImageResource(image.resourceId)
                is Image.Url -> Glide.with(itemView.context)
                    .load(image.url)
                    .into(binding.ivHint)

                Image.None -> Unit
            }

            binding.ivHint.setOnClickListener {
                onImageClicked?.invoke()
            }
        }
    }

    sealed interface Image {
        data class Url(val url: String) : Image
        data class Drawable(val resourceId: Int) : Image
        data object None : Image
    }
}