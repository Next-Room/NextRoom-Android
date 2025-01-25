package com.nextroom.nextroom.presentation.ui.adminmain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nextroom.nextroom.presentation.databinding.ItemThemeBinding
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

class ThemesAdapter(
    private val onThemeClicked: (Int) -> Unit
) : ListAdapter<ThemeInfoPresentation, ThemesAdapter.ThemeViewHolder>(diffUtil) {

    class ThemeViewHolder(
        private val binding: ItemThemeBinding,
        onStartGame: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var item: ThemeInfoPresentation
        private val context = binding.root.context

        init {
            binding.root.setOnClickListener { onStartGame(item.id) }
        }

        fun bind(themeInfo: ThemeInfoPresentation) = with(binding) {
            item = themeInfo
            tvThemeName.text = themeInfo.title
            //TODO : 이미지처리
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
