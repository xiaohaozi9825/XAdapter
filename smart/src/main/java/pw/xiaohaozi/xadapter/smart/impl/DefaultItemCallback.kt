package pw.xiaohaozi.xadapter.smart.impl

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

/**
 * [DiffUtil.ItemCallback] 的默认实现：同一引用视为同一 item；内容相等性使用 [equals]。
 */
class DefaultItemCallback<D> : DiffUtil.ItemCallback<D>() {
    /** 默认以引用相等判断是否为同一数据项。 */
    override fun areItemsTheSame(oldItem: D & Any, newItem: D & Any): Boolean {
        return oldItem === newItem
    }

    /** 在 [areItemsTheSame] 为 true 时，用 [equals] 比较内容是否变化。 */
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: D & Any, newItem: D & Any): Boolean {
        return oldItem == newItem
    }
}
