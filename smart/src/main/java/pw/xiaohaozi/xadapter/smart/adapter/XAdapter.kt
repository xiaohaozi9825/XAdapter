package pw.xiaohaozi.xadapter.smart.adapter

import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.lifecycle.DefaultLifecycleObserver
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
import androidx.core.util.size

/**
 * XAdapter基类，提供Adapter基础功能
 *
 * VB：布局文件
 * D：数据类型
 * R：返回对象类型，写子类型，如SmartAdapter<VB : ViewBinding, D>() : XAdapter<VB, D, SmartAdapter<VB, D>>()
 *
 * 描述：负责adapter生命周期分发，ViewHolder创建，类型提供者管理等工作
 * 作者：小耗子
 * 创建时间：2024/6/8 14:59
 */
open class XAdapter<VB : ViewBinding, D, out R : XAdapter<VB, D, R>> : Adapter<XHolder<VB>>(), CoroutineScope {
    private val TAG = "XAdapter"

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
    private var itemTypeCallback: (XAdapter<VB, D, R>.(data: D, position: Int) -> Int?)? = null

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
            ?: throw XAdapterException("没有找到viewType = $viewType 的 provide，请检查adapter中各布局与数据集合中数据类型是否匹配。")
        val holder: XHolder<*> = provide.onCreateViewHolder(parent, viewType)
        onViewHolderChanges.tryNotify { onCreated(provide, holder) }
        provide.onCreatedViewHolder(holder)
        @Suppress("UNCHECKED_CAST")
        return holder as XHolder<VB>
    }

    override fun onBindViewHolder(holder: XHolder<VB>, position: Int) {
        bindViewHolder(holder, position, null)
    }

    override fun onBindViewHolder(holder: XHolder<VB>, position: Int, payloads: MutableList<Any>) {
        bindViewHolder(holder, position, payloads)
    }

    private fun bindViewHolder(holder: XHolder<VB>, position: Int, payloads: MutableList<Any>?) {
        if (position < 0) return
        holder.resetBindScope()
        holder.data = getData(position)
        val provide = providers[holder.itemViewType] ?: providers[getItemViewType(position)]
        ?: throw XAdapterException("没有找到 itemViewType = ${holder.itemViewType} 的 Provider")
        onViewHolderChanges.tryNotify { if (payloads == null) onBinding(holder, position) else onBinding(holder, position, payloads) }
        provideViewHolder(provide, holder, holder.data, position, payloads)
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
    fun setDataList(list: MutableList<*>) {
        if (this::asyncListDiffer.isInitialized) throw XAdapterException("由于您设置了differ，请使用submitList()方法跟新数据！")
        @Suppress("UNCHECKED_CAST")
        datas = list as MutableList<D>
    }

    /**
     * 获取数据
     */
    fun getDataList(): MutableList<D> {
        return if (this::asyncListDiffer.isInitialized) asyncListDiffer.currentList else datas
    }

    /**
     * 修改数据
     * 框架内使用，只对datas赋值，不更新列表
     */
    @Deprecated("后续将不再使用该方法", ReplaceWith("this.setDataList(list)"), DeprecationLevel.WARNING)
    fun setData(list: MutableList<*>) {
        setDataList(list)
    }

    /**
     * 获取数据
     */
    @Deprecated("后续将不再使用该方法", ReplaceWith("this.getDataList()"), DeprecationLevel.WARNING)
    fun getData(): MutableList<D> {
        return getDataList()
    }


    /**
     * 获取指定位置的数据
     * @param position adapterPosition
     * @return 返回参数类型可能时对应的泛型D，也有可能是特殊类型，因此这里使用Any类型
     */
    fun getData(position: Int): Any? {
        val headerCount = if (hasHeader) getHeaderProviderCount() else 0
        val dataSize = getDataList().size
        if (defaultPageTriple != null) {
            return when {
                hasHeader && hasFooter -> {
                    //头布局
                    if (position < headerCount) headers[position].third
                    //脚布局
                    else if (position >= headerCount + 1) {
                        val footerPosition = position - headerCount - 1
                        footers[footerPosition].third
                    }
                    //缺省页
                    else defaultPageTriple!!.third
                }

                hasHeader && !hasFooter -> {
                    //头布局
                    if (position < headerCount) headers[position].third
                    //缺省页
                    else defaultPageTriple!!.third
                }

                !hasHeader && hasFooter -> {
                    //缺省页
                    if (position == 0)
                        defaultPageTriple!!.third
                    //脚布局
                    else
                        footers[position - 1].third
                }

                else -> {
                    //缺省页
                    defaultPageTriple!!.third
                }
            }

        }
        if (hasEmpty) {//如果当前显示了空布局
            return when {
                hasHeader && hasFooter -> {
                    //头布局
                    if (position < headerCount)
                        headers[position].third
                    //脚布局
                    else if (position >= headerCount + 1) {
                        val footerPosition = position - headerCount - 1
                        footers[footerPosition].third
                    }

                    //空布局
                    else
                        emptyTriple!!.third
                }

                hasHeader && !hasFooter -> {
                    //头布局
                    if (position < headerCount)
                        headers[position].third
                    //空布局
                    else
                        emptyTriple!!.third
                }

                !hasHeader && hasFooter -> {
                    //空布局
                    if (position == 0)
                        emptyTriple!!.third
                    //脚布局
                    else
                        footers[position - 1].third
                }

                else -> {
                    emptyTriple!!.third
                }
            }

        }

        return if (hasHeader && position < headerCount)
            headers[position].third
        else if (hasFooter && position >= headerCount + dataSize) {
            val footerPosition = position - headerCount - dataSize
            footers[footerPosition].third
        } else {
            val dataPosition = getDataPosition(position)
            getDataList()[dataPosition]
        }
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
        val dataSize = getDataList().size
        val headerCount = if (hasHeader) getHeaderProviderCount() else 0

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
        if (hasEmpty) {//如果当前adapter显示了空布局
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
            val data = getDataList()[pos]
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
    var hasEmpty: Boolean = false
        set(value) {
            if (defaultPageTriple == null && emptyTriple != null && datas.isEmpty()) {
                if (field) {
                    //如果上一次是隐藏状态：field = true    datas.isEmpty() = false ; 有数据
                    // value = false -> 存值 return  ；   value = true return
                    //如果上一次是显示状态，field = true   datas.isEmpty() = true ;
                    //value = false -> 存值、隐藏 ； value = true return
                    if (!value) {
                        notifyItemRemoved(if (hasHeader) getHeaderProviderCount() else 0)
                    }
                } else {
                    //如果上一次是隐藏状态： field = false   datas.isEmpty() = true ; 无数据
                    //  value = false -> return ； value = true 存值、显示
                    //如果上一次是隐藏状态：field = false   datas.isEmpty() = false ;有数据
                    // value = false -> return ；  value = true 存值
                    if (value) {
                        notifyItemInserted(if (hasHeader) getHeaderProviderCount() else 0)
                    }
                }
            }
            field = value
        }
        get() {
            return defaultPageTriple == null && emptyTriple != null && field && datas.isEmpty()
        }

    override fun getItemCount(): Int {
        val headerCount = if (hasHeader) getHeaderProviderCount() else 0
        val footerCount = if (hasFooter) getFooterProviderCount() else 0
        val dataCount = getDataList().size
        if (defaultPageTriple != null) return headerCount + footerCount + 1
        if (hasEmpty) return headerCount + footerCount + 1
        return headerCount + footerCount + dataCount
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

        return Int.MIN_VALUE + providers.size
    }

    /**************************************************************************/
    /**************************  对外扩展方法  ********************************/
    /**************************************************************************/

    /**
     * 向当前adapter增加一个Holder提供者
     * @param itemType Holder对于itemType，≤0的数值被框架占用，使用者必须使用>0的整数
     * @param provider 对应的Holder提供者
     */
    fun addProvider(provider: TypeProvider<*, *>, itemType: Int? = null): R {
        if (itemType != null && itemType < 0) throw XAdapterException("itemType 必须为非负整数，不能为: $itemType")
        val type = itemType ?: automaticallyItemType(provider)
        providers[type] = provider
        @Suppress("UNCHECKED_CAST")
        return this as R
    }

    /**
     * 自定义类型
     */
    fun customItemType(call: XAdapter<VB, D, R>.(data: D, position: Int) -> Int?): R {
        itemTypeCallback = call
        @Suppress("UNCHECKED_CAST")
        return this as R
    }

    /**
     * 追加Provider
     */
    open operator fun plus(provider: TypeProvider<*, *>): R {
        addProvider(provider)
        @Suppress("UNCHECKED_CAST")
        return this as R
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
    fun setEmptyProvider(provider: XProvider<*, *>, footer: EMPTY, showOnFirstLoad: Boolean) {
        val type = automaticallyItemType(provider)
        hasEmpty = showOnFirstLoad
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

    /**
     * 按 [ViewBinding] 类型显示已注册的缺省页（与 [setDefaultPage] 中布局泛型一致）。
     * 若找到且与当前展示的缺省页不同，则替换中间内容区并刷新。
     */
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
            hasEmpty = false
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


    /**
     * 添加头布局
     * 改方法可动态设置，设置后直接展示。
     *
     * @param tag 备用字段，可用于标记，或数据存储与传递
     * @param init 初始化时回调，可在此设置事件监听操作
     * @param create 创建ViewHolder后调用，可用于初始化item
     * @param bind 绑定视图时调用
     */
    inline fun <reified vb : ViewBinding> addHeader(
        tag: String = "",
        noinline init: (XProvider<vb, HEADER>.() -> Unit)? = null,
        noinline create: (XProvider<vb, HEADER>.(holder: XHolder<vb>) -> Unit)? = null,
        noinline bind: (XProvider<vb, HEADER>.(holder: XHolder<vb>, data: HEADER) -> Unit)? = null,
    ): R {
        val provider = object : XProvider<vb, HEADER>(this) {

            override fun onCreated(holder: XHolder<vb>) {
                create?.invoke(this, holder)
            }

            override fun onBind(scope: CoroutineScope, holder: XHolder<vb>, data: HEADER, position: Int) {
                bind?.invoke(this, holder, data)
            }

            override fun isFixedViewType(): Boolean {
                return true
            }
        }
        addHeaderProvider(provider, HEADER(tag))
        init?.invoke(provider)
        @Suppress("UNCHECKED_CAST")
        return this as R
    }


    /**
     * 删除指定头布局
     */
    inline fun <reified T : ViewBinding> removeHeader(): R {
        removeHeaderProvider<T>()
        @Suppress("UNCHECKED_CAST")
        return this as R
    }

    /**
     * 添加脚布局
     * 改方法可动态设置，设置后直接展示。
     *
     * @param tag 备用字段，可用于标记，或数据存储与传递
     * @param init 初始化时回调，可在此设置事件监听操作
     * @param create 创建ViewHolder后调用，可用于初始化item
     * @param bind 绑定视图时调用
     */
    inline fun <reified vb : ViewBinding> addFooter(
        tag: String = "",
        noinline init: (XProvider<vb, FOOTER>.() -> Unit)? = null,
        noinline create: (XProvider<vb, FOOTER>.(holder: XHolder<vb>) -> Unit)? = null,
        noinline bind: (XProvider<vb, FOOTER>.(holder: XHolder<vb>, data: FOOTER) -> Unit)? = null,
    ): R {
        val provider = object : XProvider<vb, FOOTER>(this) {

            override fun onCreated(holder: XHolder<vb>) {
                create?.invoke(this, holder)
            }

            override fun onBind(scope: CoroutineScope, holder: XHolder<vb>, data: FOOTER, position: Int) {
                bind?.invoke(this, holder, data)
            }

            override fun isFixedViewType(): Boolean {
                return true
            }
        }
        addFooterProvider(provider, FOOTER(tag))
        init?.invoke(provider)
        @Suppress("UNCHECKED_CAST")
        return this as R
    }


    /**
     * 删除指定脚布局
     */
    inline fun <reified T : ViewBinding> removeFooter(): R {
        removeFooterProvider<T>()
        @Suppress("UNCHECKED_CAST")
        return this as R
    }

    /**
     * 设置空布局
     * 改方法需在初始化adapter时设置，设置后并不直接显示。
     * 当adapter无数据时自动显示，有数据时自动隐藏。
     *
     * @param showOnFirstLoad 首次加载时数据为空是否显示该布局
     * @param init 初始化时回调，可在此设置事件监听操作
     * @param create 创建ViewHolder后调用，可用于初始化item
     * @param bind 绑定视图时调用
     */
    inline fun <reified vb : ViewBinding> setEmpty(
        showOnFirstLoad: Boolean = false,
        noinline init: (XProvider<vb, EMPTY>.() -> Unit)? = null,
        noinline create: (XProvider<vb, EMPTY>.(holder: XHolder<vb>) -> Unit)? = null,
        noinline bind: (XProvider<vb, EMPTY>.(holder: XHolder<vb>) -> Unit)? = null,
    ): R {
        val provider = object : XProvider<vb, EMPTY>(this) {

            override fun isFixedViewType(): Boolean {
                return true
            }

            override fun onBind(scope: CoroutineScope, holder: XHolder<vb>, data: EMPTY, position: Int) {
                bind?.invoke(this, holder)
            }

            override fun onCreated(holder: XHolder<vb>) {
                create?.invoke(this, holder)
            }

        }
        setEmptyProvider(provider, EMPTY, showOnFirstLoad)
        init?.invoke(provider)
        @Suppress("UNCHECKED_CAST")
        return this as R
    }


    /**
     * 设置缺省页
     * 改方法需在初始化adapter时设置，设置后并不直接显示。
     * 显示与隐藏需调用：
     * @see showDefaultPage
     * @see hintDefaultPage
     *
     * @param tag 备用字段，可用于标记，或数据存储与传递
     * @param init 初始化时回调，可在此设置事件监听操作
     * @param create 创建ViewHolder后调用，可用于初始化item
     * @param bind 绑定视图时调用
     */
    inline fun <reified vb : ViewBinding> setDefaultPage(
        tag: Any = "",
        noinline init: (XProvider<vb, DEFAULT_PAGE>.() -> Unit)? = null,
        noinline create: (XProvider<vb, DEFAULT_PAGE>.(holder: XHolder<vb>) -> Unit)? = null,
        noinline bind: (XProvider<vb, DEFAULT_PAGE>.(holder: XHolder<vb>, data: DEFAULT_PAGE) -> Unit)? = null,
    ): R {
        val provider = object : XProvider<vb, DEFAULT_PAGE>(this) {

            override fun isFixedViewType(): Boolean {
                return true
            }

            override fun onBind(scope: CoroutineScope, holder: XHolder<vb>, data: DEFAULT_PAGE, position: Int) {
                bind?.invoke(this, holder, data)
            }

            override fun onCreated(holder: XHolder<vb>) {
                create?.invoke(this, holder)
            }

        }
        setDefaultPageProvider(provider, DEFAULT_PAGE(tag))
        init?.invoke(provider)
        @Suppress("UNCHECKED_CAST")
        return this as R
    }

    /**************************************************************************/
    /**************************   生命周期同步   ******************************/
    /**************************************************************************/
    override fun onViewRecycled(holder: XHolder<VB>) {
        Log.i(TAG, "onViewRecycled: $holder")
        val viewType = holder.itemViewType
        tryNotifyProvider(viewType) { onViewRecycled(holder) }
        holder.data = null
    }

    override fun onFailedToRecycleView(holder: XHolder<VB>): Boolean {
        val viewType = holder.itemViewType
        tryNotifyProvider(viewType) { onFailedToRecycleView(holder) }
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
        tryNotifyAllProvider { onAdapterAttachedToRecyclerView(recyclerView) }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        Log.i(TAG, "onDetachedFromRecyclerView: ")
        coroutineContext.cancel()
        recyclerView.removeOnAttachStateChangeListener(rvOnAttachStateChangeListener)
        onRecyclerViewChanges.tryNotify { onDetachedFromRecyclerView(recyclerView) }
        tryNotifyAllProvider { onAdapterDetachedFromRecyclerView(recyclerView) }
        lifecycleOwner?.lifecycle?.removeObserver(defaultLifecycleObserver)
        this.recyclerView = null
    }

    override fun onViewAttachedToWindow(holder: XHolder<VB>) {
        Log.i(TAG, "onViewAttachedToWindow: ")
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            val isFixed = providers[getItemViewType(holder.adapterPosition)]?.isFixedViewType() ?: false
            layoutParams.isFullSpan = isFixed
        }
        onViewChanges.tryNotify { onViewAttachedToWindow(holder) }
        val viewType = holder.itemViewType
        tryNotifyProvider(viewType) { onHolderAttachedToWindow(holder) }
    }

    override fun onViewDetachedFromWindow(holder: XHolder<VB>) {
        //该方法比onViewRecycled先调
        Log.i(TAG, "onViewDetachedFromWindow: $holder")
        holder.cancelHolderCoroutineChildren()//取消holder中的所有子协程
        onViewChanges.tryNotify { onViewDetachedFromWindow(holder) }
        val viewType = holder.itemViewType
        tryNotifyProvider(viewType) { onHolderDetachedFromWindow(holder) }
    }


    private fun tryNotifyProvider(viewType: Int, action: TypeProvider<VB, D>.() -> Unit) {
        try {
            action.invoke(providers[viewType] as TypeProvider<VB, D>)
        } catch (e: Exception) {
            Log.e(TAG, "tryNotifyProvider: ", e)
        }
    }

    private fun tryNotifyAllProvider(action: TypeProvider<VB, D>.() -> Unit) {
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

    /** 注册 ViewHolder 创建/绑定阶段的全局监听（先于具体 Provider 逻辑分发）。 */
    fun addOnViewHolderChanges(change: OnViewHolderChanges) {
        onViewHolderChanges.add(change)
    }

    /** 注册 Adapter 附着/脱离 [RecyclerView] 的监听。 */
    fun addOnRecyclerViewChanges(change: OnRecyclerViewChanges) {
        onRecyclerViewChanges.add(change)
    }

    /** 注册单个 item 视图附着/脱离窗口的监听。 */
    fun addOnViewChanges(change: OnViewChanges<VB>) {
        onViewChanges.add(change)
    }

    inner class RVOnAttachStateChangeListener : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            Log.i(TAG, "onViewAttachedToWindow: ")
            onRecyclerViewAttachStateChanges.tryNotify { onViewAttachedToWindow(v as RecyclerView) }
            tryNotifyAllProvider { onRecyclerViewAttachedToWindow(v as RecyclerView) }
        }

        //这里可以用来监听activity销毁
        override fun onViewDetachedFromWindow(v: View) {
            Log.i(TAG, "onViewDetachedFromWindow: ")
            onRecyclerViewAttachStateChanges.tryNotify { onViewDetachedFromWindow(v as RecyclerView) }
            tryNotifyAllProvider { onRecyclerViewDetachedFromWindow(v as RecyclerView) }
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

