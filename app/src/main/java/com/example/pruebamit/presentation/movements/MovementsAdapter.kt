package com.example.pruebamit.presentation.movements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebamit.databinding.ItemMovementBinding
import com.example.pruebamit.domain.model.Movement

class MovementsAdapter : ListAdapter<Movement, MovementsAdapter.MovementViewHolder>(MovementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementViewHolder {
        val binding = ItemMovementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovementViewHolder(
        private val binding: ItemMovementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movement: Movement) {
            binding.apply {
                tvRecipientName.text = movement.recipientName
                tvReason.text = movement.reason
                tvDateTime.text = movement.getDateTime()
                tvLocation.text = "📍 ${movement.location}"
                tvAmount.text = movement.getFormattedAmount()
            }
        }
    }

    private class MovementDiffCallback : DiffUtil.ItemCallback<Movement>() {
        override fun areItemsTheSame(oldItem: Movement, newItem: Movement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movement, newItem: Movement): Boolean {
            return oldItem == newItem
        }
    }
}