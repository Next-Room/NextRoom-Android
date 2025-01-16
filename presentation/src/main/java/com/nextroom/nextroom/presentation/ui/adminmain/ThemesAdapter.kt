package com.nextroom.nextroom.presentation.ui.adminmain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mangbaam.commonutil.DateTimeUtil
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.databinding.ItemThemeBinding
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

class ThemesAdapter(
    private val onThemeClicked: (Int) -> Unit,
    private val onClickUpdate: (Int) -> Unit,
) : ListAdapter<ThemeInfoPresentation, ThemesAdapter.ThemeViewHolder>(diffUtil) {

    class ThemeViewHolder(
        private val binding: ItemThemeBinding,
        onStartGame: (Int) -> Unit,
        onClickUpdate: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var item: ThemeInfoPresentation
        private val context = binding.root.context

        init {
            binding.root.setOnClickListener { onStartGame(item.id) }
            binding.btnUpdate.setOnClickListener { onClickUpdate(item.id) }
        }

        fun bind(themeInfo: ThemeInfoPresentation) = with(binding) {
            item = themeInfo
            tvThemeName.text = themeInfo.title
            val updatedAt = if (themeInfo.recentUpdated != 0L) {
                DateTimeUtil().longToDateString(themeInfo.recentUpdated, pattern = "yyyy-MM-dd")
            } else {
                context.getString(R.string.common_not_exists)
            }
            tvRecentUpdate.text = context.getString(R.string.admin_main_updated_at, updatedAt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder(
            ItemThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onThemeClicked,
            onClickUpdate,
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
