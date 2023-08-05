package io.github.ivan8m8.courierhelper.data.utils

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class BasicDiffUtilItemCallback<T: BasicDiffUtilItem> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.identifier == newItem.identifier

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}

interface BasicDiffUtilItem {
    val identifier: Any
}