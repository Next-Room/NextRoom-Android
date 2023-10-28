package com.nextroom.nextroom.presentation.ui.purchase

import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nextroom.nextroom.domain.model.Ticket
import com.nextroom.nextroom.presentation.databinding.ItemTicketBinding
import com.nextroom.nextroom.presentation.extension.strikeThrow
import java.text.DecimalFormat

class TicketAdapter(
    private val onClickTicket: (Ticket) -> Unit,
) : ListAdapter<TicketUiModel, TicketAdapter.TicketViewHolder>(diffUtil) {

    class TicketViewHolder(
        private val binding: ItemTicketBinding,
        private val onClickTicket: (Ticket) -> Unit,
    ) : ViewHolder(binding.root) {
        private lateinit var item: TicketUiModel

        init {
            binding.root.setOnClickListener {
                if (!item.subscribing) onClickTicket(item.toDomain())
            }
        }

        fun bind(ticket: TicketUiModel) = with(binding) {
            item = ticket

            tvName.text = ticket.plan
            tvDescription.text = ticket.description
            tvOriginPrice.isVisible = ticket.originPrice != null
            ticket.originPrice?.let {
                tvOriginPrice.text = DecimalFormat("#,###원").format(it)
                tvOriginPrice.strikeThrow()
            }
            val intervalUnitText = "/월"
            val sellPriceText = DecimalFormat("#,###원$intervalUnitText").format(ticket.sellPrice)
            tvSellPrice.text = SpannableString(sellPriceText).apply {
                val i = sellPriceText.lastIndexOf(intervalUnitText)
                setSpan(
                    AbsoluteSizeSpan(14, true),
                    i,
                    i + intervalUnitText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }
            tvSubscribingBadge.isVisible = ticket.subscribing
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketViewHolder(binding, ::onClickTicket.get())
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<TicketUiModel>() {
            override fun areItemsTheSame(oldItem: TicketUiModel, newItem: TicketUiModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TicketUiModel, newItem: TicketUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
