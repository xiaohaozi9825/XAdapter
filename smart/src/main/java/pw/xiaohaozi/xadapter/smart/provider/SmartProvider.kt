package pw.xiaohaozi.xadapter.smart.provider

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.SmartHolder
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * 类型提供者
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/8 14:29
 */
abstract class SmartProvider<VB : ViewBinding, D>(override val adapter: SmartAdapter<*, *>) : TypeProvider<VB, D> {
    val TAG = "SmartProvider"
    abstract fun onCreated(holder: SmartHolder<VB>)
    abstract fun onBind(holder: SmartHolder<VB>, data: D, position: Int)
    open fun onBind(holder: SmartHolder<VB>, data: D, position: Int, payloads: List<Any?>) {
        onBind(holder, data, position)
    }

    override fun getEmployerAdapter(): SmartAdapter<*, *> {
        return adapter
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmartHolder<VB> {
        Log.i(TAG, "onCreateViewHolder: ")
        return SmartHolder(smartCreateViewBinding(parent))
    }

    override fun onCreatedViewHolder(holder: SmartHolder<*>) {
        onCreated(holder as SmartHolder<VB>)
    }

    override fun onBindViewHolder(holder: SmartHolder<*>, data: Any?, position: Int) {
        onBind(holder as SmartHolder<VB>, data as D, position)
    }

    override fun onBindViewHolder(holder: SmartHolder<*>, data: Any?, position: Int, payloads: List<Any?>) {
        onBind(holder as SmartHolder<VB>, data as D, position, payloads)
    }

    override fun onViewRecycled(holder: SmartHolder<VB>) {

    }

    override fun onFailedToRecycleView(holder: SmartHolder<VB>) {

    }

    override fun onHolderAttachedToWindow(holder: SmartHolder<VB>) {
        if (isFixedViewType()) setFullSpan(holder)
    }

    override fun onHolderDetachedFromWindow(holder: SmartHolder<VB>) {

    }

    override fun onAdapterAttachedToRecyclerView(recyclerView: RecyclerView) {

    }

    override fun onAdapterDetachedFromRecyclerView(recyclerView: RecyclerView) {

    }

    override fun onRecyclerViewAttachedToWindow(recyclerView: RecyclerView) {

    }

    override fun onViewRecyclerDetachedFromWindow(recyclerView: RecyclerView) {

    }
    //反射创建ViewBinding实例
    //A : Adapter<*>, VH : Holder<VB>, VB : ViewBinding, D
    @Suppress("UNCHECKED_CAST")
    private fun smartCreateViewBinding(parent: ViewGroup): VB {
        val genericSuperclass = this.javaClass.genericSuperclass as? ParameterizedType ?: throw RuntimeException("必须明确指定VB泛型类型")
        val find = genericSuperclass.actualTypeArguments.find {
            (it as? Class<*>)?.run { ViewBinding::class.java.isAssignableFrom(this) } ?: false
        }
        return smartCreateViewBinding(
            parent, find as? Class<*> ?: throw NullPointerException("没有找到 ViewBinding 的子类 VB")
        )
    }

    //反射创建ViewBinding实例
    @Suppress("UNCHECKED_CAST")
    private fun smartCreateViewBinding(parent: ViewGroup, clazz: Class<*>): VB {
        val method: Method = clazz.getMethod(
            "inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java
        )
        return method.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
    }

    override fun getItemViewType(): Int? {
        adapter.providers.forEach { key, value ->
            if (value == this) return key
        }
        return null
    }

    protected open fun setFullSpan(holder: RecyclerView.ViewHolder) {
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            layoutParams.isFullSpan = true
        }
    }
}

