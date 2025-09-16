package pw.xiaohaozi.xadapter.smart.adapter

import android.util.Log
import androidx.core.util.forEach
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.entity.DEFAULT_PAGE
import pw.xiaohaozi.xadapter.smart.entity.EMPTY
import pw.xiaohaozi.xadapter.smart.entity.FOOTER
import pw.xiaohaozi.xadapter.smart.entity.HEADER
import pw.xiaohaozi.xadapter.smart.ext.OnProviderBindHolder
import pw.xiaohaozi.xadapter.smart.ext.OnProviderCreatedHolder
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.impl.AdapterSelectedImpl
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.impl.SmartDataImpl
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy
import pw.xiaohaozi.xadapter.smart.proxy.SelectedProxy
import pw.xiaohaozi.xadapter.smart.proxy.SmartDataProxy
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer
import pw.xiaohaozi.xadapter.smart.ext.OnBindParams as OnBindParams1

/**
 * Adapter集
 * 描述：包含了XAdapter基础功能，数据操作功能，选择功能，事件监听功能
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 9:10
 */
open class SmartAdapter<VB : ViewBinding, D>(
    val dataProxy: SmartDataProxy<SmartAdapter<VB, D>, VB, D> = SmartDataImpl(), //
    val eventProxy: EventProxy<SmartAdapter<VB, D>, VB, D> = EventImpl(),//
    val selectedProxy: SelectedProxy<SmartAdapter<VB, D>, VB, D> = AdapterSelectedImpl()//
) : XAdapter<VB, D>(),//继承Adapter
    XEmployer, //宿主
    SmartDataProxy<SmartAdapter<VB, D>, VB, D> by dataProxy,//数据
    EventProxy<SmartAdapter<VB, D>, VB, D> by eventProxy,//
    SelectedProxy<SmartAdapter<VB, D>, VB, D> by selectedProxy //
{
    init {
        initProxy()
    }

    private fun initProxy() {
        initProxy(this)
    }

    override var employer: SmartAdapter<VB, D>
        get() = this
        set(value) {
            throw XAdapterException("employer不允许设置")
        }

    final override fun initProxy(employer: SmartAdapter<VB, D>) {
        dataProxy.initProxy(employer)
        eventProxy.initProxy(employer)
        selectedProxy.initProxy(employer)
    }

    override fun getEmployerAdapter(): XAdapter<VB, D> {
        return this
    }


    override operator fun plus(provider: TypeProvider<*, *>): SmartAdapter<VB, D> {
        addProvider(provider)
        return this
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
        noinline init: (SmartProvider<VB, D, vb, HEADER>.() -> Unit)? = null,
        noinline create: (SmartProvider<VB, D, vb, HEADER>.(holder: XHolder<vb>) -> Unit)? = null,
        noinline bind: (SmartProvider<VB, D, vb, HEADER>.(holder: XHolder<vb>) -> Unit)? = null,
    ): SmartAdapter<VB, D> {
        val provider = object : SmartProvider<VB, D, vb, HEADER>(this) {

            override fun onCreated(holder: XHolder<vb>) {
                create?.invoke(this, holder)
            }

            override fun onBind(holder: XHolder<vb>, data: HEADER, position: Int) {
                bind?.invoke(this, holder)
            }

            override fun isFixedViewType(): Boolean {
                return true
            }
        }
        addHeaderProvider(provider, HEADER(tag))
        init?.invoke(provider)
        return this
    }


    /**
     * 删除指定头布局
     */
    inline fun <reified T : ViewBinding> removeHeader(): SmartAdapter<VB, D> {
        removeHeaderProvider<T>()
        return this
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
        noinline init: (SmartProvider<VB, D, vb, FOOTER>.() -> Unit)? = null,
        noinline create: (SmartProvider<VB, D, vb, FOOTER>.(holder: XHolder<vb>) -> Unit)? = null,
        noinline bind: (SmartProvider<VB, D, vb, FOOTER>.(holder: XHolder<vb>) -> Unit)? = null,
    ): SmartAdapter<VB, D> {
        val provider = object : SmartProvider<VB, D, vb, FOOTER>(this) {

            override fun onCreated(holder: XHolder<vb>) {
                create?.invoke(this, holder)
            }

            override fun onBind(holder: XHolder<vb>, data: FOOTER, position: Int) {
                bind?.invoke(this, holder)
            }

            override fun isFixedViewType(): Boolean {
                return true
            }
        }
        addFooterProvider(provider, FOOTER(tag))
        init?.invoke(provider)
        return this
    }


    /**
     * 删除指定脚布局
     */
    inline fun <reified T : ViewBinding> removeFooter(): SmartAdapter<VB, D> {
        removeFooterProvider<T>()
        return this
    }

    /**
     * 设置空布局
     * 改方法需在初始化adapter时设置，设置后并不直接显示。
     * 当adapter无数据时自动显示，有数据时自动隐藏。
     *
     * @param init 初始化时回调，可在此设置事件监听操作
     * @param create 创建ViewHolder后调用，可用于初始化item
     * @param bind 绑定视图时调用
     */
    inline fun <reified vb : ViewBinding> setEmpty(
        noinline init: (SmartProvider<VB, D, vb, EMPTY>.() -> Unit)? = null,
        noinline create: (SmartProvider<VB, D, vb, EMPTY>.(holder: XHolder<vb>) -> Unit)? = null,
        noinline bind: (SmartProvider<VB, D, vb, EMPTY>.(holder: XHolder<vb>) -> Unit)? = null,
    ): SmartAdapter<VB, D> {
        val provider = object : SmartProvider<VB, D, vb, EMPTY>(this) {

            override fun isFixedViewType(): Boolean {
                return true
            }

            override fun onBind(holder: XHolder<vb>, data: EMPTY, position: Int) {
                bind?.invoke(this, holder)
            }

            override fun onCreated(holder: XHolder<vb>) {
                create?.invoke(this, holder)
            }

        }
        setEmptyProvider(provider, EMPTY)
        init?.invoke(provider)
        return this
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
        noinline init: (SmartProvider<VB, D, vb, DEFAULT_PAGE>.() -> Unit)? = null,
        noinline create: (SmartProvider<VB, D, vb, DEFAULT_PAGE>.(holder: XHolder<vb>) -> Unit)? = null,
        noinline bind: (SmartProvider<VB, D, vb, DEFAULT_PAGE>.(holder: XHolder<vb>) -> Unit)? = null,
    ): SmartAdapter<VB, D> {
        val provider = object : SmartProvider<VB, D, vb, DEFAULT_PAGE>(this) {

            override fun isFixedViewType(): Boolean {
                return true
            }

            override fun onBind(holder: XHolder<vb>, data: DEFAULT_PAGE, position: Int) {
                bind?.invoke(this, holder)
            }

            override fun onCreated(holder: XHolder<vb>) {
                create?.invoke(this, holder)
            }

        }
        setDefaultPageProvider(provider, DEFAULT_PAGE(tag))
        init?.invoke(provider)
        return this
    }


    /**
     * 多布局切换
     * 返回Provider
     */
    inline fun <pvb : VB, pd : D> withType(
        isFixed: Boolean? = null,
        itemType: Int? = null,
        crossinline init: (SmartProvider<VB, D, pvb, pd>.() -> Unit) = {},
        crossinline created: OnProviderCreatedHolder<VB, D, pvb, pd> = {},
        crossinline bind: OnProviderBindHolder<VB, D, pvb, pd>,
    ): SmartProvider<VB, D, pvb, pd> {
        val provider = object : SmartProvider<VB, D, pvb, pd>(this) {

            override fun onCreated(holder: XHolder<pvb>) {
                created.invoke(this, holder)
            }

            override fun onBind(holder: XHolder<pvb>, data: pd, position: Int) {
            }

            override fun onBind(holder: XHolder<pvb>, data: pd, position: Int, payloads: List<Any?>) {
                bind.invoke(this, OnBindParams1(holder, data, position, payloads))
            }

            override fun isFixedViewType(): Boolean {
                return isFixed ?: false
            }

        }
        addProvider(provider, itemType)
        init.invoke(provider)
        return provider
    }
}