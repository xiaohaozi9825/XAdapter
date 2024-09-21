package pw.xiaohaozi.xadapter.smart.adapter

import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.entity.MultiItemEntity
import pw.xiaohaozi.xadapter.smart.holder.SmartHolder
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import java.lang.reflect.ParameterizedType
import kotlin.math.absoluteValue

/**
 * Adapter基类，提供Adapter基础功能
 * 描述：负责adapter生命周期分发，ViewHolder创建，类型提供者管理等工作
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/8 14:59
 */
open class SmartAdapter<VB : ViewBinding, D> : Adapter<SmartHolder<VB>>() {
    companion object {
        const val TAG = "SmartAdapter"
    }


    var datas: MutableList<D> = mutableListOf()
    val providers: SparseArray<TypeProvider<*, *>> by lazy { SparseArray() }
    private val onViewHolderChanges: ArrayList<OnViewHolderChanges> = arrayListOf()
    private val onRecyclerViewChanges: ArrayList<OnRecyclerViewChanges> = arrayListOf()
    private val onViewChanges: ArrayList<OnViewChanges<VB>> = arrayListOf()
    private val onRecyclerViewAttachStateChanges: ArrayList<OnRecyclerViewAttachStateChanges> = arrayListOf()
    private val rvOnAttachStateChangeListener = RVOnAttachStateChangeListener()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmartHolder<VB> {
        val provide = providers[viewType]
        val holder: SmartHolder<*> = provide.onCreateViewHolder(parent, viewType)
        onViewHolderChanges.tryNotify { onCreated(provide, holder) }
        provide.onCreatedViewHolder(holder)
        Log.i("SmartProvider", "onCreateViewHolder: holder = $holder")
        return holder as SmartHolder<VB>
    }

    override fun onBindViewHolder(holder: SmartHolder<VB>, position: Int) {
        val viewType = getItemViewType(position)
        val provide = providers[viewType]
        Log.i("SmartProvider", "onBindViewHolder: viewType = $viewType -- provide = $provide")
        onViewHolderChanges.tryNotify { onBinding(holder, position) }
        provide.onBindViewHolder(holder, datas[position], position)
    }


    override fun onBindViewHolder(
        holder: SmartHolder<VB>, position: Int, payloads: MutableList<Any>
    ) {
        val viewType = getItemViewType(position)
        val provide = providers[viewType]
        Log.i("SmartProvider", "onBindViewHolder: viewType = $viewType -- provide = $provide")
        provide.onBindViewHolder(holder, datas[position], position, payloads)
    }

    override fun getItemViewType(position: Int): Int {
        val data = datas[position] ?: return 0
        if (data is MultiItemEntity) {
            val itemViewType = data.getItemViewType()
            if (itemViewType <= 0) throw XAdapterException("data.getItemViewType() 必须为正整数。而当前值为：“$itemViewType”")
            return itemViewType
        }
        val clazz = data::class.java
        providers.forEach { key, value ->
            val genericSuperclass = value.javaClass.genericSuperclass as? ParameterizedType ?: throw RuntimeException("必须明确指定 D 泛型类型")
            if (genericSuperclass.actualTypeArguments.any { it == clazz }) {
                return key
            }
        }
        return 0
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    fun addProvider(viewType: Int, provide: TypeProvider<*, *>): SmartAdapter<VB, D> {
        providers.put(viewType, provide)
        return this
    }


    override fun onViewRecycled(holder: SmartHolder<VB>) {
        tryNotifyProvider { onViewRecycled(holder) }
    }

    override fun onFailedToRecycleView(holder: SmartHolder<VB>): Boolean {
        tryNotifyProvider { onFailedToRecycleView(holder) }
        return super.onFailedToRecycleView(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnAttachStateChangeListener(rvOnAttachStateChangeListener)
        onRecyclerViewChanges.tryNotify { onAttachedToRecyclerView(recyclerView) }
        tryNotifyProvider { onAdapterAttachedToRecyclerView(recyclerView) }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.removeOnAttachStateChangeListener(rvOnAttachStateChangeListener)
        onRecyclerViewChanges.tryNotify { onDetachedFromRecyclerView(recyclerView) }
        tryNotifyProvider { onAdapterDetachedFromRecyclerView(recyclerView) }
    }

    override fun onViewAttachedToWindow(holder: SmartHolder<VB>) {
        Log.i(TAG, "onViewAttachedToWindow: ")
        onViewChanges.tryNotify { onViewAttachedToWindow(holder) }
        tryNotifyProvider { onHolderAttachedToWindow(holder) }
    }

    override fun onViewDetachedFromWindow(holder: SmartHolder<VB>) {
        Log.i(TAG, "onViewDetachedFromWindow: ")
        onViewChanges.tryNotify { onViewDetachedFromWindow(holder) }
        tryNotifyProvider { onHolderDetachedFromWindow(holder) }
    }


    private fun tryNotifyProvider(action: TypeProvider<VB, D>.() -> Unit) {
        providers.forEach { key, provider ->
            try {
                action.invoke(provider as TypeProvider<VB, D>)
            } catch (e: Exception) {
                Log.e(TAG, "tryNotifyProvider: ", e)
            }
        }
    }

    private fun <T> ArrayList<T>.tryNotify(action: T.() -> Unit) {
        forEach {
            try {
                action.invoke(it)
            } catch (e: Exception) {
                Log.e(TAG, "tryNotify: ", e)
            }
        }
    }

    fun addOnViewHolderChanges(change: OnViewHolderChanges) {
        onViewHolderChanges.add(change)
    }

    fun addOnRecyclerViewChanges(change: OnRecyclerViewChanges) {
        onRecyclerViewChanges.add(change)
    }

    fun addOnViewChanges(change: OnViewChanges<VB>) {
        onViewChanges.add(change)
    }

    inner class RVOnAttachStateChangeListener : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            Log.i(TAG, "onViewAttachedToWindow: ")
            onRecyclerViewAttachStateChanges.tryNotify { onViewAttachedToWindow(v as RecyclerView) }
            tryNotifyProvider { onRecyclerViewAttachedToWindow(v as RecyclerView) }
        }

        //这里可以用来监听activity销毁
        override fun onViewDetachedFromWindow(v: View) {
            Log.i(TAG, "onViewDetachedFromWindow: ")
            onRecyclerViewAttachStateChanges.tryNotify { onViewDetachedFromWindow(v as RecyclerView) }
            tryNotifyProvider { onViewRecyclerDetachedFromWindow(v as RecyclerView) }
        }
    }

    interface OnViewHolderChanges {
        fun onCreated(provide: TypeProvider<*, *>, holder: SmartHolder<*>)
        fun onBinding(holder: SmartHolder<*>, position: Int)
    }

    interface OnRecyclerViewChanges {
        fun onAttachedToRecyclerView(recyclerView: RecyclerView)
        fun onDetachedFromRecyclerView(recyclerView: RecyclerView)
    }

    interface OnViewChanges<VB : ViewBinding> {
        fun onViewAttachedToWindow(holder: SmartHolder<VB>)
        fun onViewDetachedFromWindow(holder: SmartHolder<VB>)
    }

    interface OnRecyclerViewAttachStateChanges {
        fun onViewAttachedToWindow(recyclerView: RecyclerView)
        fun onViewDetachedFromWindow(recyclerView: RecyclerView)
    }

}

