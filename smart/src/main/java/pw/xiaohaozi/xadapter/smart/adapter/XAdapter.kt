package pw.xiaohaozi.xadapter.smart.adapter

import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.job
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.entity.DEFAULT_PAGE
import pw.xiaohaozi.xadapter.smart.entity.EMPTY
import pw.xiaohaozi.xadapter.smart.entity.FOOTER
import pw.xiaohaozi.xadapter.smart.entity.HEADER
import pw.xiaohaozi.xadapter.smart.entity.XMultiItemEntity
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import pw.xiaohaozi.xadapter.smart.provider.XProvider
import java.lang.reflect.ParameterizedType
import kotlin.coroutines.CoroutineContext

/**
 * Adapter基类，提供Adapter基础功能
 * 描述：负责adapter生命周期分发，ViewHolder创建，类型提供者管理等工作
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/8 14:59
 */
open class XAdapter<VB : ViewBinding, D> : Adapter<XHolder<VB>>(), CoroutineScope {
    companion object {
        const val TAG = "XAdapter"
    }


    var recyclerView: RecyclerView? = null
    var lifecycleOwner: LifecycleOwner? = null

    //协程
    override val coroutineContext: CoroutineContext
            by lazy { SupervisorJob(lifecycleOwner?.lifecycleScope?.coroutineContext?.job) + Dispatchers.Main + CoroutineName("XAdapterCoroutine") }

    //diff
    internal lateinit var asyncListDiffer: AsyncListDiffer<D>

    //数据集
    private var datas: MutableList<D> = mutableListOf()

    //viewHolder 提供者
    val providers: SparseArray<TypeProvider<*, *>> by lazy { SparseArray() }

    //activity生命周期回调
    private val defaultLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            this@XAdapter.recyclerView?.adapter = null
        }
    }

    //item类型回调
    private var itemTypeCallback: (XAdapter<VB, D>.(data: D, position: Int) -> Int?)? = null

    /************Adapter生命周期相关回调******************/
    private val onViewHolderChanges: ArrayList<OnViewHolderChanges> = arrayListOf()
    private val onRecyclerViewChanges: ArrayList<OnRecyclerViewChanges> = arrayListOf()
    private val onViewChanges: ArrayList<OnViewChanges<VB>> = arrayListOf()
    private val onRecyclerViewAttachStateChanges: ArrayList<OnRecyclerViewAttachStateChanges> = arrayListOf()
    private val rvOnAttachStateChangeListener = RVOnAttachStateChangeListener()

    /************特殊布局******************/
    val headers = arrayListOf<Triple<XProvider<*, *>, Int, HEADER>>()
    val footers = arrayListOf<Triple<XProvider<*, *>, Int, FOOTER>>()
    val defaultPages = arrayListOf<Triple<XProvider<*, *>, Int, DEFAULT_PAGE>>()
    var defaultPageTriple: Triple<XProvider<*, *>, Int, DEFAULT_PAGE>? = null
    var emptyTriple: Triple<XProvider<*, *>, Int, EMPTY>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XHolder<VB> {
        val provide = providers[viewType]
        val holder: XHolder<*> = provide.onCreateViewHolder(parent, viewType)
        onViewHolderChanges.tryNotify { onCreated(provide, holder) }
        provide.onCreatedViewHolder(holder)
        Log.i(TAG, "onCreateViewHolder: holder = $holder")
        return holder as XHolder<VB>
    }

    override fun onBindViewHolder(holder: XHolder<VB>, position: Int) {
        bindViewHolder(holder, position, null)
    }

    override fun onBindViewHolder(holder: XHolder<VB>, position: Int, payloads: MutableList<Any>) {
        bindViewHolder(holder, position, payloads)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    private fun bindViewHolder(holder: XHolder<VB>, position: Int, payloads: MutableList<Any>?) {
        if (position < 0) return
        val provide = providers[holder.itemViewType] ?: providers[getItemViewType(position)]
        ?: throw XAdapterException("没有找到 itemViewType = ${holder.itemViewType} 的 Provider")
        onViewHolderChanges.tryNotify { if (payloads == null) onBinding(holder, position) else onBinding(holder, position, payloads) }
        val headerCount = if (hasHeader) getHeaderProviderCount() else 0
        val dataSize = getData().size
        if (defaultPageTriple != null) {
            when {
                hasHeader && hasFooter -> {
                    //头布局
                    if (position < headerCount) provideViewHolder(provide, holder, headers[position].third, position, payloads)
                    //脚布局
                    else if (position >= headerCount + 1) {
                        val footerPosition = position - headerCount - 1
                        provideViewHolder(provide, holder, footers[footerPosition].third, footerPosition, payloads)
                    }
                    //缺省页
                    else provideViewHolder(provide, holder, defaultPageTriple!!.third, 0)
                }

                hasHeader && !hasFooter -> {
                    //头布局
                    if (position < headerCount) provideViewHolder(provide, holder, headers[position].third, position, payloads)
                    //缺省页
                    else provideViewHolder(provide, holder, defaultPageTriple!!.third, 0, payloads)
                }

                !hasHeader && hasFooter -> {
                    //缺省页
                    if (position == 0)
                        provideViewHolder(provide, holder, defaultPageTriple!!.third, 0, payloads)
                    //脚布局
                    else
                        provideViewHolder(provide, holder, footers[position - 1].third, position - 1, payloads)
                }

                else -> {
                    //缺省页
                    provideViewHolder(provide, holder, defaultPageTriple!!.third, 0, payloads)
                }
            }
            return
        }
        if (emptyTriple != null && dataSize == 0) {
            when {
                hasHeader && hasFooter -> {
                    //头布局
                    if (position < headerCount)
                        provideViewHolder(provide, holder, headers[position].third, position, payloads)
                    //脚布局
                    else if (position >= headerCount + 1) {
                        val footerPosition = position - headerCount - 1
                        provideViewHolder(provide, holder, footers[footerPosition].third, footerPosition, payloads)
                    }

                    //空布局
                    else
                        provideViewHolder(provide, holder, emptyTriple!!.third, 0, payloads)
                }

                hasHeader && !hasFooter -> {
                    //头布局
                    if (position < headerCount)
                        provideViewHolder(provide, holder, headers[position].third, position, payloads)
                    //空布局
                    else
                        provideViewHolder(provide, holder, emptyTriple!!.third, 0, payloads)
                }

                !hasHeader && hasFooter -> {
                    //空布局
                    if (position == 0)
                        provideViewHolder(provide, holder, emptyTriple!!.third, 0, payloads)
                    //脚布局
                    else
                        provideViewHolder(provide, holder, footers[position - 1].third, position - 1, payloads)
                }

                else -> {
                    provideViewHolder(provide, holder, emptyTriple!!.third, 0, payloads)
                }
            }
            return
        }

        if (hasHeader && position < headerCount)
            provideViewHolder(provide, holder, headers[position].third, position, payloads)
        else if (hasFooter && position >= headerCount + dataSize) {
            val footerPosition = position - headerCount - dataSize
            provideViewHolder(provide, holder, footers[footerPosition].third, footerPosition, payloads)
        } else {
            val dataPosition = getDataPosition(position)
            val d = getData()[dataPosition]
            provideViewHolder(provide, holder, d, dataPosition, payloads)
        }
    }

    /**
     * 是否启用了Differ模式
     */
    fun isDifferMode() = this::asyncListDiffer.isInitialized

    /**
     * 绑定Activity或Fragment生命周期
     * @param lifecycle
     */
    fun bindLifecycle(lifecycle: LifecycleOwner) {
        lifecycleOwner = lifecycle
    }

    /**
     * 修改数据
     * 框架内使用，只对datas赋值，不更新列表
     */
    fun setData(list: MutableList<*>) {
        if (this::asyncListDiffer.isInitialized) throw XAdapterException("由于您设置了differ，请使用submitList()方法跟新数据！")
        datas = list as MutableList<D>
    }

    /**
     * 获取数据
     */
    fun getData(): MutableList<D> {
        return if (this::asyncListDiffer.isInitialized) asyncListDiffer.currentList else datas
    }

    private fun provideViewHolder(
        provide: TypeProvider<*, *>,
        holder: XHolder<VB>,
        data: Any?,
        position: Int,
        payloads: List<Any>? = null
    ) {
        if (payloads == null) provide.onBindViewHolder(holder, data, position)
        else provide.onBindViewHolder(holder, data, position, payloads)
    }

    /**
     * 获取item类型
     * @param position 相对adapter
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        val dataSize = getData().size
        val headerCount = getHeaderProviderCount()

        //如果设置了缺省页，则使用缺省页
        if (defaultPageTriple != null) {
            return when {
                hasHeader && hasFooter -> {
                    //头布局
                    if (position < headerCount) headers[position].second
                    //脚布局
                    else if (position >= headerCount + 1) footers[position - headerCount - 1].second
                    //缺省页
                    else defaultPageTriple!!.second
                }

                hasHeader && !hasFooter -> {
                    //头布局
                    if (position < headerCount) headers[position].second
                    //缺省页
                    else defaultPageTriple!!.second
                }

                !hasHeader && hasFooter -> {
                    //缺省页
                    if (position == 0) defaultPageTriple!!.second
                    //脚布局
                    else footers[position - 1].second
                }

                else -> {
                    defaultPageTriple!!.second
                }
            }
        }
        if (emptyTriple != null && dataSize == 0) {
            return when {
                hasHeader && hasFooter -> {
                    //头布局
                    if (position < headerCount) headers[position].second
                    //脚布局
                    else if (position >= headerCount + 1) footers[position - headerCount - 1].second
                    //空布局
                    else emptyTriple!!.second
                }

                hasHeader && !hasFooter -> {
                    //头布局
                    if (position < headerCount) headers[position].second
                    //空布局
                    else emptyTriple!!.second
                }

                !hasHeader && hasFooter -> {
                    //空布局
                    if (position == 0) emptyTriple!!.second
                    //脚布局
                    else footers[position - 1].second
                }

                else -> {
                    emptyTriple!!.second
                }
            }
        }

        //头布局
        if (hasHeader && position < headerCount) return headers[position].second
        //脚布局
        else if (hasFooter && position >= headerCount + dataSize) return footers[position - headerCount - dataSize].second
        //用户自定义布局
        else {
            val pos = getDataPosition(position)
            val data = getData()[pos]
            return getCustomType(data, position)
        }
    }

    private fun getCustomType(data: D, position: Int): Int {
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
            providers.forEach { key, value ->
                Log.i(TAG, "getItemViewType: key = $key")
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
                    ?: throw XAdapterException("必须明确指定 D 泛型类型")
                if (genericSuperclass.actualTypeArguments.any { it == clazz }) {
                    return key
                }
            }
        }
        return 0
    }

    var hasHeader: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            if (value) {
                notifyItemRangeInserted(0, getHeaderProviderCount())
            } else {
                notifyItemRangeRemoved(0, getHeaderProviderCount())
            }
        }
    var hasFooter: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            val itemCount = itemCount
            val footersCount = getFooterProviderCount()
            if (value) {
                notifyItemRangeInserted(itemCount - footersCount, footersCount)
            } else {
                notifyItemRangeRemoved(itemCount - footersCount, footersCount)
            }
        }

    override fun getItemCount(): Int {
        val headerCount = if (hasHeader) getHeaderProviderCount() else 0
        val footerCount = if (hasFooter) getFooterProviderCount() else 0
        val dataCount = getData().size
        if (defaultPageTriple != null) return headerCount + footerCount + 1
        if (emptyTriple != null && dataCount == 0) return headerCount + footerCount + 1
        return headerCount + footerCount + dataCount
    }


    /**************************************************************************/
    /**************************  对外扩展方法  ********************************/
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

    /**
     * 自定义类型
     */
    fun customItemType(call: XAdapter<VB, D>.(data: D, position: Int) -> Int?): XAdapter<VB, D> {
        itemTypeCallback = call
        return this
    }

    /**
     * 追加Provider
     */
    open operator fun plus(provider: TypeProvider<*, *>): XAdapter<VB, D> {
        addProvider(provider)
        return this
    }

    /**
     * 获取头布局数量
     */
    fun getHeaderProviderCount() = headers.size

    /**
     * 获取脚布局数量
     */
    fun getFooterProviderCount() = footers.size

    /**
     * adapterPosition转换为dataPosition
     * 当有头布局时，回调函数中的position均为adapterPosition，如果需要对应数据的索引，这里需要做一次计算
     */
    fun getDataPosition(adapterPosition: Int): Int {
        return if (!hasHeader) adapterPosition
        else adapterPosition - getHeaderProviderCount()
    }

    /**
     * dataPosition转换为adapterPosition
     */
    fun getAdapterPosition(dataPosition: Int): Int {
        return if (!hasHeader) dataPosition
        else dataPosition + getHeaderProviderCount()
    }

    /**
     * 添加头布局
     */
    fun addHeaderProvider(provider: XProvider<*, *>, header: HEADER) {
        val type = automaticallyItemType(provider)
        providers[type] = provider
        headers.add(0, Triple(provider, type, header))
        if (hasHeader) notifyItemInserted(0)
    }

    /**
     * 移除头布局
     */
    inline fun <reified T : ViewBinding> removeHeaderProvider() {
        headers.find {
            val genericSuperclass = it.first.javaClass.genericSuperclass as? ParameterizedType
            genericSuperclass?.actualTypeArguments?.contains(T::class.java) == true
        }?.let {
            val position = headers.indexOf(it)
            providers.remove(it.second)
            headers.remove(it)
            if (hasHeader) notifyItemRemoved(position)
        }
    }

    /**
     * 添加脚布局
     */
    fun addFooterProvider(provider: XProvider<*, *>, footer: FOOTER) {
        val type = automaticallyItemType(provider)
        providers[type] = provider
        footers.add(Triple(provider, type, footer))
        if (hasFooter) notifyItemInserted(itemCount - 1)
    }

    /**
     * 移除脚布局
     */
    inline fun <reified T : ViewBinding> removeFooterProvider() {
        footers.find {
            val genericSuperclass = it.first.javaClass.genericSuperclass as? ParameterizedType
            genericSuperclass?.actualTypeArguments?.contains(T::class.java) == true
        }?.let {
            val position = itemCount - getFooterProviderCount() + footers.indexOf(it)
            providers.remove(it.second)
            footers.remove(it)
            if (hasFooter) notifyItemRemoved(position)
        }
    }

    /**
     * 设置空布局
     * 初始化时设置，当数据为空时自动展示
     */
    fun setEmptyProvider(provider: XProvider<*, *>, footer: EMPTY) {
        val type = automaticallyItemType(provider)
        emptyTriple = Triple(provider, type, footer)
        providers[type] = provider
    }


    /**
     * 设置缺省页
     * 初始化时设置，调用showDefaultPage()与hintDefaultPage()方法展示和隐藏
     */
    fun setDefaultPageProvider(provider: XProvider<*, *>, page: DEFAULT_PAGE) {
        val itemType = -providers.size() - 1
        defaultPages.add(Triple(provider, itemType, page))
        providers[itemType] = provider
    }

    inline fun <reified T : ViewBinding> showDefaultPage() {
        val defaultPageTriple = defaultPages.findLast {
            val genericSuperclass = it.first.javaClass.genericSuperclass as? ParameterizedType
            genericSuperclass?.actualTypeArguments?.contains(T::class.java) == true
        }
        //找到了对应的缺省页，且与上次展示的不一样时，才刷新
        if (defaultPageTriple != null && defaultPageTriple != this.defaultPageTriple) {
            val itemCount = itemCount
            val headerCount = if (hasHeader) getHeaderProviderCount() else 0
            val footerCount = if (hasFooter) getFooterProviderCount() else 0
            notifyItemRangeRemoved(headerCount, itemCount - headerCount - footerCount)
            this.defaultPageTriple = defaultPageTriple
            notifyItemInserted(headerCount)
        }
    }

    /**
     * 显示缺省页
     */
    fun showDefaultPage(tag: Any) {
        val defaultPageTriple = defaultPages.find { it.third.tag == tag }
        //找到了对应的缺省页，且与上次展示的不一样时，才刷新
        if (defaultPageTriple != null && defaultPageTriple != this.defaultPageTriple) {
            val itemCount = itemCount
            val headerCount = if (hasHeader) getHeaderProviderCount() else 0
            val footerCount = if (hasFooter) getFooterProviderCount() else 0
            notifyItemRangeRemoved(headerCount, itemCount - headerCount - footerCount)
            this.defaultPageTriple = defaultPageTriple
            notifyItemInserted(headerCount)
        }
    }

    /**
     * 隐藏缺省页
     */
    fun hintDefaultPage() {
        defaultPageTriple = null
        val headerCount = if (hasHeader) getHeaderProviderCount() else 0
        notifyItemRemoved(headerCount)
    }


    /**
     * 刷新列表所有item
     * 处于效率考虑，该方法只刷新可见的item
     */
    fun notifyAllItemChanged(payload: Any? = null) {
        notifyItemRangeChanged(0, itemCount, payload)
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

        return Int.MIN_VALUE + providers.size()
    }


    /**************************************************************************/
    /**************************   生命周期同步   ******************************/
    /**************************************************************************/
    override fun onViewRecycled(holder: XHolder<VB>) {
        Log.i(TAG, "onViewRecycled: $holder")
        tryNotifyProvider { onViewRecycled(holder) }
    }

    override fun onFailedToRecycleView(holder: XHolder<VB>): Boolean {
        tryNotifyProvider { onFailedToRecycleView(holder) }
        return super.onFailedToRecycleView(holder)
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        lifecycleOwner?.lifecycle?.addObserver(defaultLifecycleObserver)
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
        Log.i(TAG, "onDetachedFromRecyclerView: ")
        coroutineContext.cancel()
        recyclerView.removeOnAttachStateChangeListener(rvOnAttachStateChangeListener)
        onRecyclerViewChanges.tryNotify { onDetachedFromRecyclerView(recyclerView) }
        tryNotifyProvider { onAdapterDetachedFromRecyclerView(recyclerView) }
        lifecycleOwner?.lifecycle?.removeObserver(defaultLifecycleObserver)
        this.recyclerView = null
    }

    override fun onViewAttachedToWindow(holder: XHolder<VB>) {
        Log.i(TAG, "onViewAttachedToWindow: ")
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            val isFixed = providers[getItemViewType(holder.bindingAdapterPosition)]?.isFixedViewType() ?: false
            layoutParams.isFullSpan = isFixed
        }
        onViewChanges.tryNotify { onViewAttachedToWindow(holder) }
        tryNotifyProvider { onHolderAttachedToWindow(holder) }
    }

    override fun onViewDetachedFromWindow(holder: XHolder<VB>) {
        //该方法比onViewRecycled先调
        Log.i(TAG, "onViewDetachedFromWindow: $holder")
        holder.coroutineContext.cancelChildren()//取消holder中的所有子协程
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
        fun onBinding(holder: XHolder<*>, position: Int, payloads: List<Any?>)
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

