package com.nextroom.nextroom.presentation.ui.background_custom

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.databinding.ItemThemeBackgroundToggleBinding
import com.nextroom.nextroom.presentation.extension.dpToPx
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation


class ThemeBackgroundToggleAdapter(
    private val onToggleClicked: (ThemeInfoPresentation) -> Unit = { _ -> },
    private val onImageClicked: (ThemeInfoPresentation) -> Unit = {}
) : ListAdapter<ThemeInfoPresentation, ThemeBackgroundToggleAdapter.ThemeViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder(
            ItemThemeBackgroundToggleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onToggleClicked = onToggleClicked,
            onImageClicked = { onImageClicked(getItem(it)) }
        )
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ThemeViewHolder(
        private val binding: ItemThemeBackgroundToggleBinding,
        private val onToggleClicked: (ThemeInfoPresentation) -> Unit = { _ -> },
        private val onImageClicked: (Int) -> Unit = {}
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imgTheme.setOnClickListener {
                val position = bindingAdapterPosition
                    .takeIf { it != NO_POSITION } ?: return@setOnClickListener
                onImageClicked(position)
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(themeInfo: ThemeInfoPresentation) = with(binding) {
            tvThemeName.text = themeInfo.title
            scBackgroundToggle.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    onToggleClicked(themeInfo)
                    return@setOnTouchListener true
                }
                false
            }

            scBackgroundToggle.isChecked = themeInfo.useTimerUrl

            imgTheme.alpha = if (themeInfo.useTimerUrl) 1f else 0.4f

            binding.tvEmptyImage.isVisible = themeInfo.themeImageUrl.isNullOrEmpty()
            if (!themeInfo.themeImageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(themeInfo.themeImageUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_error)
                    .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(8.dpToPx)))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            binding.flPreview.isVisible = false
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            binding.flPreview.isVisible = true
                            return false
                        }
                    })
                    .into(binding.imgTheme)
            }
        }
    }


    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ThemeInfoPresentation>() {
            override fun areItemsTheSame(
                oldItem: ThemeInfoPresentation,
                newItem: ThemeInfoPresentation,
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ThemeInfoPresentation,
                newItem: ThemeInfoPresentation,
            ) = oldItem == newItem
        }
    }
}
