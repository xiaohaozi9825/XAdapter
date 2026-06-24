package pw.xiaohaozi.xadapter.smart.provider

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 类型提供者
 * 描述：
 * 作者：小耗子
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/8 14:29
 */
abstract class XProvider<VB : ViewBinding, D>(override val adapter: XAdapter<*, *, *>) : TypeProvider<VB, D> {
    private val TAG = "XProvider"

    /**
     * 在 [adapter] 作用域内启动协程（与 RecyclerView / Adapter 生命周期一致），
     * 与单次 item 绑定对齐的异步请使用 [onBind] 的首参 [scope]。
     */
    protected fun adapterLaunch(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = adapter.launch(context = context, block = block)

    abstract fun onCreated(holder: XHolder<VB>)

    /**
     * 数据绑定
     * @param scope 与当前一次 bind 对齐，请使用 `scope.launch { }`。
     * @param holder viewHolder
     * @param data 当前数据
     * @param position 在adapter中的索引。注意：有特殊布局的时候，不要直接用该position获取dataList中的数据，需要用getDataPosition(position)方法转换。
     */
    abstract fun onBind(scope: CoroutineScope, holder: XHolder<VB>, data: D, position: Int)

    /**
     * 数据绑定
     * @param scope 与当前一次 bind 对齐，请使用 `scope.launch { }`。
     * @param holder viewHolder
     * @param data 当前数据
     * @param position 在adapter中的索引。注意：有特殊布局的时候，不要直接用该position获取dataList中的数据，需要用getDataPosition(position)方法转换。
     * @param payloads 局部刷新使用
     */
    open fun onBind(scope: CoroutineScope, holder: XHolder<VB>, data: D, position: Int, payloads: List<Any?>) {
        onBind(scope, holder, data, position)
    }

    override fun getEmployerAdapter(): XAdapter<*, *, *> {
        return adapter
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XHolder<VB> {
        Log.i(TAG, "onCreateViewHolder: ")
        return XHolder(adapter, smartCreateViewBinding(parent))
    }

    override fun onCreatedViewHolder(holder: XHolder<*>) {
        @Suppress("UNCHECKED_CAST")
        onCreated(holder as XHolder<VB>)
    }

    override fun onBindViewHolder(holder: XHolder<*>, data: Any?, position: Int) {
        @Suppress("UNCHECKED_CAST")
        val h = holder as XHolder<VB>
        onBind(h.bindCoroutineScope(), h, data as D, position)
    }

    override fun onBindViewHolder(holder: XHolder<*>, data: Any?, position: Int, payloads: List<Any?>) {
        @Suppress("UNCHECKED_CAST")
        val h = holder as XHolder<VB>
        onBind(h.bindCoroutineScope(), h, data as D, position, payloads)
    }

    override fun onViewRecycled(holder: XHolder<VB>) {

    }

    override fun onFailedToRecycleView(holder: XHolder<VB>) {

    }

    override fun onHolderAttachedToWindow(holder: XHolder<VB>) {
    }

    override fun onHolderDetachedFromWindow(holder: XHolder<VB>) {

    }

    override fun onAdapterAttachedToRecyclerView(recyclerView: RecyclerView) {

    }

    override fun onAdapterDetachedFromRecyclerView(recyclerView: RecyclerView) {

    }

    override fun onRecyclerViewAttachedToWindow(recyclerView: RecyclerView) {

    }

    override fun onRecyclerViewDetachedFromWindow(recyclerView: RecyclerView) {

    }

    //反射创建ViewBinding实例
    //A : Adapter<*>, VH : Holder<VB>, VB : ViewBinding, D
    private fun smartCreateViewBinding(parent: ViewGroup): VB {
        val genericSuperclass =
            this.javaClass.genericSuperclass as? ParameterizedType ?: throw RuntimeException("必须明确指定VB泛型类型")
        val find = genericSuperclass.actualTypeArguments.findLast {
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
}
