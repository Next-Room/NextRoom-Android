package com.nextroom.nextroom.presentation.ui.background_custom

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.databinding.ItemThemeBackgroundToggleBinding
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

class ThemeBackgroundToggleAdapter(
    private val onToggleClicked: (ThemeInfoPresentation) -> Unit = { _ -> }
) : ListAdapter<ThemeInfoPresentation, ThemeBackgroundToggleAdapter.ThemeViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder(
            ItemThemeBackgroundToggleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onToggleClicked
        )
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ThemeViewHolder(
        private val binding: ItemThemeBackgroundToggleBinding,
        private val onToggleClicked: (ThemeInfoPresentation) -> Unit = { _ -> }
    ) : RecyclerView.ViewHolder(binding.root) {

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

            binding.tvEmptyImage.isVisible = themeInfo.themeImageUrl.isNullOrEmpty()
            Glide.with(binding.root.context)
                .load(themeInfo.themeImageUrl ?: "")
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .into(binding.imgTheme)
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
