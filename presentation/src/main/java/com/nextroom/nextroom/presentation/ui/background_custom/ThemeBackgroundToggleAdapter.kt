package com.nextroom.nextroom.presentation.ui.background_custom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nextroom.nextroom.presentation.databinding.ItemThemeBackgroundToggleBinding
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

class ThemeBackgroundToggleAdapter(
) : ListAdapter<ThemeInfoPresentation, ThemeBackgroundToggleAdapter.ThemeViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder(
            ItemThemeBackgroundToggleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ThemeViewHolder(
        private val binding: ItemThemeBackgroundToggleBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(themeInfo: ThemeInfoPresentation) = with(binding) {
            tvThemeName.text = themeInfo.title
            //TODO : 이미지처리
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
