package com.nextroom.nextroom.presentation.ui.adminmain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.databinding.ItemThemeBinding
import com.nextroom.nextroom.presentation.extension.dpToPx
import com.nextroom.nextroom.presentation.extension.isSamePath

class ThemesAdapter(
    private val onThemeClicked: (Int) -> Unit
) : ListAdapter<ThemesAdapter.ThemeInfo, ThemesAdapter.ThemeViewHolder>(diffUtil) {

    class ThemeViewHolder(
        private val binding: ItemThemeBinding,
        onStartGame: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var item: ThemeInfo
        private val context = binding.root.context

        init {
            binding.root.setOnClickListener { onStartGame(item.id) }
        }

        fun bind(themeInfo: ThemeInfo) = with(binding) {
            item = themeInfo
            tvThemeName.text = themeInfo.title
            Glide.with(binding.root)
                .load(themeInfo.imageUrl)
                .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(8.dpToPx)))
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.imgTheme)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder(
            ItemThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onThemeClicked
        )
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    data class ThemeInfo(
        val id: Int,
        val title: String,
        val imageUrl: String?
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ThemeInfo) return false

            return id == other.id && title == other.title && imageUrl.isSamePath(other.imageUrl)
        }

        override fun hashCode(): Int {
            return 31 * id + title.hashCode() + (imageUrl?.hashCode() ?: 0)
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ThemeInfo>() {
            override fun areItemsTheSame(
                oldItem: ThemeInfo,
                newItem: ThemeInfo,
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ThemeInfo,
                newItem: ThemeInfo,
            ) = oldItem == newItem
        }
    }
}
