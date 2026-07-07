package pw.xiaohaozi.xadapter.smart.provider

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer

/**
 *
 * 描述：
 * 作者：小耗子
 * 创建时间：2024/6/8 14:30
 */
interface TypeProvider<VB : ViewBinding, D> : XEmployer {
    val adapter: XAdapter<*, *, *>

    /**
     * ViewHolder创建成功
     * 这里可以做一些初始化操作
     */
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XHolder<*>

    /** [onCreateViewHolder] 之后调用，可对 Holder 做一次性初始化。 */
    fun onCreatedViewHolder(holder: XHolder<*>)

    /** 将 [data] 绑定到 [holder]；无 payload 的整项刷新。 */
    fun onBindViewHolder(holder: XHolder<*>, data: Any?, position: Int)

    /** 将 [data] 绑定到 [holder]；支持 [payloads] 局部刷新。 */
    fun onBindViewHolder(holder: XHolder<*>, data: Any?, position: Int, payloads: List<Any?>)

    /** 是否为占满整行/整列的固定类型（如 Header），用于 Grid/Staggered 跨列等。 */
    fun isFixedViewType(): Boolean

    /** 当前 Provider 在 [XAdapter.providers] 中注册的 `itemType`；未注册时返回 null。 */
    fun getItemViewType(): Int?

    /**
     * View被回收时回调
     * 可用于释放资源
     */
    fun onViewRecycled(holder: XHolder<VB>)

    /**
     * 回收失败时的补救回调；返回是否由本 Provider 自行处理（语义同 [RecyclerView.Adapter.onFailedToRecycleView] 链路上的扩展点）。
     */
    fun onFailedToRecycleView(holder: XHolder<VB>)

    /**
     * ViewHolder附着到Window
     */
    fun onHolderAttachedToWindow(holder: XHolder<VB>)

    /**
     * ViewHolder 离开 Window
     */
    fun onHolderDetachedFromWindow(holder: XHolder<VB>)

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
    fun onRecyclerViewDetachedFromWindow(recyclerView: RecyclerView)

}
