package pw.xiaohaozi.xadapter.smart.adapter

import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.entity.XMultiItemEntity
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import java.lang.reflect.ParameterizedType
import java.util.LinkedList

/**
 * Adapter基类，提供Adapter基础功能
 * 描述：负责adapter生命周期分发，ViewHolder创建，类型提供者管理等工作
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/8 14:59
 */
open class XAdapter<VB : ViewBinding, D> : Adapter<XHolder<VB>>() {
    companion object {
        const val TAG = "XAdapter"
    }


    var datas: MutableList<D> = mutableListOf()
    val providers: SparseArray<TypeProvider<*, *>> by lazy { SparseArray() }
    private var itemTypeCallback: (XAdapter<VB, D>.(data: D, position: Int) -> Int?)? = null

    private val onViewHolderChanges: ArrayList<OnViewHolderChanges> = arrayListOf()
    private val onRecyclerViewChanges: ArrayList<OnRecyclerViewChanges> = arrayListOf()
    private val onViewChanges: ArrayList<OnViewChanges<VB>> = arrayListOf()
    private val onRecyclerViewAttachStateChanges: ArrayList<OnRecyclerViewAttachStateChanges> =        arrayListOf()
    private val rvOnAttachStateChangeListener = RVOnAttachStateChangeListener()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XHolder<VB> {
        val provide = providers[viewType]
        val holder: XHolder<*> = provide.onCreateViewHolder(parent, viewType)
        onViewHolderChanges.tryNotify { onCreated(provide, holder) }
        provide.onCreatedViewHolder(holder)
        Log.i(TAG, "onCreateViewHolder: holder = $holder")
        return holder as XHolder<VB>
    }

    override fun onBindViewHolder(holder: XHolder<VB>, position: Int) {
        val viewType = getItemViewType(position)
        val provide = providers[viewType]
        Log.i(TAG, "onBindViewHolder: viewType = $viewType -- provide = $provide")
        onViewHolderChanges.tryNotify { onBinding(holder, position) }
        provide.onBindViewHolder(holder, datas[position], position)
    }


    override fun onBindViewHolder(
        holder: XHolder<VB>, position: Int, payloads: MutableList<Any>
    ) {
        val viewType = getItemViewType(position)
        val provide = providers[viewType]
        Log.i(TAG, "onBindViewHolder: viewType = $viewType -- provide = $provide")
        provide.onBindViewHolder(holder, datas[position], position, payloads)
    }

    override fun getItemViewType(position: Int): Int {
        val data = datas[position]
        if (itemTypeCallback != null) {
            val type = itemTypeCallback?.invoke(this, data, position)
            if (type != null) return type
        }
        if (data is XMultiItemEntity) {
            val itemViewType = data.getItemViewType()
            if (itemViewType <= 0) throw XAdapterException("data.getItemViewType() 必须为正整数。而当前值为：“$itemViewType”")
            return itemViewType
        }
        //当有数据为空时
        if (data == null) {
            //如果有 itemType ==0的情况，则返回0
            //否则返回itemType 满足条件的最小值
            if (providers.get(0) != null) return 0
            for (index in 0 until providers.size()) {
                val key = providers.keyAt(index)
            }

            providers.forEach { key, value ->
                Log.i(TAG, "getItemViewType: key = $key")
//                val kClass = value::class
//                kClass.supertypes.forEach {
//                    Log.i(TAG, "getItemViewType: supertype = ${it}")
//                    it.arguments.forEach {
//                        Log.i(TAG, "getItemViewType: arguments = ${it}")
//                        Log.i(TAG, "getItemViewType: arguments = ${it.type?.isMarkedNullable}")//泛型是否可空
//                    }
//                }
                try {
                    //使用kotlin 反射获取第一个泛型类型为可空类型的provider 对应的key
                    //需要用到implementation "org.jetbrains.kotlin:kotlin-reflect:1.7.10"库
                    //如果没有添加该库，则会走进入异常捕获，返回第一个 provider的key
                    val isMarkedNullable = value::class.supertypes.any {
                        it.arguments.any { it.type?.isMarkedNullable == true }
                    }
                    if (isMarkedNullable) return key
                } catch (e: KotlinReflectionNotSupportedError) {
                    Log.i(TAG, "getItemViewType: KotlinReflectionNotSupportedError key = $key")
                    return key
                }
            }

        } else {
            val clazz = data!!::class.java
            providers.forEach { key, value ->
                val genericSuperclass = value.javaClass.genericSuperclass as? ParameterizedType
                    ?: throw RuntimeException("必须明确指定 D 泛型类型")
                if (genericSuperclass.actualTypeArguments.any { it == clazz }) {
                    return key
                }
            }
        }

        return 0
    }

    override fun getItemCount(): Int {
        return datas.size
    }


    /**************************************************************************/
    /**************************  对外扩展方法    ********************************/
    /**************************************************************************/

    /**
     * 向当前adapter增加一个Holder提供者
     * @param itemType Holder对于itemType，≤0的数值被框架占用，使用者必须使用>0的整数
     * @param provider 对应的Holder提供者
     */
    fun addProvider(provider: TypeProvider<*, *>, itemType: Int? = null): XAdapter<VB, D> {
        if (itemType != null && itemType < 0) throw XAdapterException("itemType 必须为非负整数，不能为: $itemType")
        val type = itemType ?: automaticallyItemType(provider)
        providers[type] = provider
        return this
    }

    fun customItemType(call: XAdapter<VB, D>.(data: D, position: Int) -> Int?): XAdapter<VB, D> {
        itemTypeCallback = call
        return this
    }

    open operator fun plus(provider: TypeProvider<*, *>): XAdapter<VB, D> {
        addProvider(provider)
        return this
    }

    /**
     * 刷新列表所有item
     * 处于效率考虑，该方法只刷新可见的item
     */
    fun notifyAllItemChanged(payload: Any? = null) {
        notifyItemRangeChanged(0, itemCount,payload)
    }


    //自动生成itemType
    private fun automaticallyItemType(provider: TypeProvider<*, *>): Int {
        val genericSuperclass = provider.javaClass.genericSuperclass as? ParameterizedType
            ?: throw XAdapterException("必须明确指定VB泛型类型")
        val find = genericSuperclass.actualTypeArguments.find {
            (it as? Class<*>)
                ?.run { XMultiItemEntity::class.java.isAssignableFrom(this) }
                ?: false
        }
        if (find != null) throw XAdapterException("provider中数据类继承自MultiItemEntity，addProvider()方法中形参itemType不能为空")

        return Int.MIN_VALUE - providers.size()
    }


    /**************************************************************************/
    /**************************   生命周期同步   ********************************/
    /**************************************************************************/
    override fun onViewRecycled(holder: XHolder<VB>) {
        tryNotifyProvider { onViewRecycled(holder) }
    }

    override fun onFailedToRecycleView(holder: XHolder<VB>): Boolean {
        tryNotifyProvider { onFailedToRecycleView(holder) }
        return super.onFailedToRecycleView(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            val defSpanSizeLookup = manager.spanSizeLookup
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val isFixed = providers[getItemViewType(position)]?.isFixedViewType() ?: false
                    return if (isFixed) manager.spanCount
                    else defSpanSizeLookup.getSpanSize(position)
                }
            }
        }
        recyclerView.addOnAttachStateChangeListener(rvOnAttachStateChangeListener)
        onRecyclerViewChanges.tryNotify { onAttachedToRecyclerView(recyclerView) }
        tryNotifyProvider { onAdapterAttachedToRecyclerView(recyclerView) }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.removeOnAttachStateChangeListener(rvOnAttachStateChangeListener)
        onRecyclerViewChanges.tryNotify { onDetachedFromRecyclerView(recyclerView) }
        tryNotifyProvider { onAdapterDetachedFromRecyclerView(recyclerView) }
    }

    override fun onViewAttachedToWindow(holder: XHolder<VB>) {
        Log.i(TAG, "onViewAttachedToWindow: ")
        onViewChanges.tryNotify { onViewAttachedToWindow(holder) }
        tryNotifyProvider { onHolderAttachedToWindow(holder) }
    }

    override fun onViewDetachedFromWindow(holder: XHolder<VB>) {
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
        fun onCreated(provide: TypeProvider<*, *>, holder: XHolder<*>)
        fun onBinding(holder: XHolder<*>, position: Int)
    }

    interface OnRecyclerViewChanges {
        fun onAttachedToRecyclerView(recyclerView: RecyclerView)
        fun onDetachedFromRecyclerView(recyclerView: RecyclerView)
    }

    interface OnViewChanges<VB : ViewBinding> {
        fun onViewAttachedToWindow(holder: XHolder<VB>)
        fun onViewDetachedFromWindow(holder: XHolder<VB>)
    }

    interface OnRecyclerViewAttachStateChanges {
        fun onViewAttachedToWindow(recyclerView: RecyclerView)
        fun onViewDetachedFromWindow(recyclerView: RecyclerView)
    }

}

