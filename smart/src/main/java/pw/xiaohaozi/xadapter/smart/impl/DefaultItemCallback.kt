package pw.xiaohaozi.xadapter.smart.impl

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class DefaultItemCallback<D> : DiffUtil.ItemCallback<D>() {
    override fun areItemsTheSame(oldItem: D & Any, newItem: D & Any): Boolean {
        return oldItem === newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: D & Any, newItem: D & Any): Boolean {
        return oldItem == newItem
    }
}