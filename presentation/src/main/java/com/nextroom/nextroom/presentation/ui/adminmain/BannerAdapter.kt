package com.nextroom.nextroom.presentation.ui.adminmain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nextroom.nextroom.domain.model.Banner
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.databinding.ItemBannerBinding

class BannerAdapter(private val onBannerClicked: (Banner) -> Unit) : ListAdapter<Banner, BannerAdapter.ViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ItemBannerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
            .let { ViewHolder(it, onBannerClicked) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemBannerBinding,
        private val onBannerClicked: (Banner) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Banner?) {
            item?.let {
                Glide.with(binding.root)
                    .load(it.imageUrl)
                    .error(R.drawable.img_banner_error)
                    .into(binding.imgBanner)

                binding.imgBanner.setOnClickListener {
                    onBannerClicked(item)
                }
            }
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Banner>() {
            override fun areItemsTheSame(oldItem: Banner, newItem: Banner): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Banner, newItem: Banner): Boolean {
                return oldItem == newItem
            }
        }
    }
}