package com.fedx.familybudget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fedx.familybudget.R
import com.fedx.familybudget.data.DataInCon
import com.fedx.familybudget.databinding.ItemIncomeConsumptionBinding

class InConAdapter(val listener: Listener) : ListAdapter<DataInCon, InConAdapter.InConViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<DataInCon>() {
        override fun areItemsTheSame(oldItem: DataInCon, newItem: DataInCon): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: DataInCon, newItem: DataInCon): Boolean {
            return oldItem == newItem
        }
    }

    class InConViewHolder(private var binding: ItemIncomeConsumptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dataInCon: DataInCon, listener: Listener) = with(binding) {
            binding.tvComment.text = dataInCon.comment
            binding.tvSum.text = dataInCon.sum
            binding.tvDate.text = dataInCon.date

            binding.ivEdit.setOnClickListener {
                listener.change(dataInCon)
            }

            binding.ivDelete.setOnClickListener {
                listener.deletePosition(dataInCon)
            }
        }

        companion object {
            fun create(parent: ViewGroup): InConViewHolder {
                return InConViewHolder(
                    ItemIncomeConsumptionBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InConViewHolder {
        return InConViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: InConViewHolder, position: Int) {
        val src = getItem(position)
        holder.bind(src, listener)
    }

    interface Listener{
        fun change(dataInCon: DataInCon)
        fun deletePosition(dataInCon: DataInCon)
    }
}
