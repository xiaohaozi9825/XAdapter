package pw.xiaohaozi.xadapter.smart.provider

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.SmartHolder
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/8 14:30
 */
interface TypeProvider<VB : ViewBinding, D> : XEmployer {
    val adapter: SmartAdapter<*, *>

    /**
     * ViewHolder创建成功
     * 这里可以做一些初始化操作
     */
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmartHolder<VB>
    fun onCreated(holder: SmartHolder<VB>)
    fun onBind(holder: SmartHolder<VB>, data: D, position: Int)
    fun onBind(holder: SmartHolder<VB>, data: D, position: Int, payloads: List<Any?>)
    fun isFixedViewType(): Boolean
    fun getItemViewType(): Int?

    /**
     * View被回收时回调
     * 可用于释放资源
     */
    fun onViewRecycled(holder: SmartHolder<VB>)

    /**
     *
     * 用于回收瞬态资源
     */
    fun onFailedToRecycleView(holder: SmartHolder<VB>)

    fun onViewAttachedToWindow(holder: SmartHolder<VB>)

    fun onViewDetachedFromWindow(holder: SmartHolder<VB>)

    fun onAttachedToRecyclerView(recyclerView: RecyclerView)

    fun onDetachedFromRecyclerView(recyclerView: RecyclerView)



}