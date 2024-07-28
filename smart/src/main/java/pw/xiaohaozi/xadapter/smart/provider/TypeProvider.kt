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
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmartHolder<*>
    fun onCreatedViewHolder(holder: SmartHolder<*>)
    fun onBindViewHolder(holder: SmartHolder<*>, data: Any?, position: Int)
    fun onBindViewHolder(holder: SmartHolder<*>, data: Any?, position: Int, payloads: List<Any?>)
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

    /**
     * ViewHolder附着到Window
     */
    fun onHolderAttachedToWindow(holder: SmartHolder<VB>)
    /**
     * ViewHolder 离开 Window
     */
    fun onHolderDetachedFromWindow(holder: SmartHolder<VB>)

    /**
     * Adapter 附着到 RecyclerView
     * 给RecyclerView 设置 Adapter 时回调
     */
    fun onAdapterAttachedToRecyclerView(recyclerView: RecyclerView)
    /**
     * Adapter 离开 RecyclerView
     * 给RecyclerView 设置其他（或null） Adapter 时回调。Activity销毁时并不会回调
     */
    fun onAdapterDetachedFromRecyclerView(recyclerView: RecyclerView)

    /**
     * RecyclerView 附着 Window
     * 一般Adapter设置该监听时，RecyclerView已经附着到了Window上了，所以该方法可能不会被回调。
     */
    fun onRecyclerViewAttachedToWindow(recyclerView: RecyclerView)
    /**
     * RecyclerView 离开 Window
     * Activity 被销毁时会调用，可以在此处进行一些资源释放工作
     */
    fun onViewRecyclerDetachedFromWindow(recyclerView: RecyclerView)

}