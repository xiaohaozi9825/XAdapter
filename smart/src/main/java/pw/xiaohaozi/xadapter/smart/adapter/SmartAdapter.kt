package pw.xiaohaozi.xadapter.smart.adapter

import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.ext.OnProviderBindHolder
import pw.xiaohaozi.xadapter.smart.ext.OnProviderCreatedHolder
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.impl.AdapterSelectedImpl
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.impl.SmartDataImpl
import pw.xiaohaozi.xadapter.smart.params.OnBindParams
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy
import pw.xiaohaozi.xadapter.smart.proxy.SelectedProxy
import pw.xiaohaozi.xadapter.smart.proxy.SmartDataProxy
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer

/**
 * Adapter集
 * 描述：包含了XAdapter基础功能，数据操作功能，选择功能，事件监听功能
 * 作者：小耗子
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 9:10
 */
open class SmartAdapter<VB : ViewBinding, D>(
    val dataProxy: SmartDataProxy<SmartAdapter<VB, D>, VB, D> = SmartDataImpl(), //
    val eventProxy: EventProxy<SmartAdapter<VB, D>, VB, D> = EventImpl(),//
    val selectedProxy: SelectedProxy<SmartAdapter<VB, D>, VB, D> = AdapterSelectedImpl()//
) : XAdapter<VB, D, SmartAdapter<VB, D>>(),//继承Adapter
    XEmployer, //宿主
    SmartDataProxy<SmartAdapter<VB, D>, VB, D> by dataProxy,//数据操作
    EventProxy<SmartAdapter<VB, D>, VB, D> by eventProxy,//时间监听
    SelectedProxy<SmartAdapter<VB, D>, VB, D> by selectedProxy //选择操作
{
    init {
        initProxy()
    }

    /** 构造时初始化各代理与当前 Adapter 的宿主关系。 */
    private fun initProxy() {
        initProxy(this)
    }

    override var employer: SmartAdapter<VB, D>
        get() = this
        set(value) {
            throw XAdapterException("employer禁止赋值")
        }

    /**
     * 将数据、事件、选择三类代理绑定到宿主 Adapter（一般为 [employer] 自身）。
     * @param employer 当前 Adapter 实例，供各代理通过 [XEmployer] 取数、发通知。
     */
    final override fun initProxy(employer: SmartAdapter<VB, D>) {
        dataProxy.initProxy(employer)
        eventProxy.initProxy(employer)
        selectedProxy.initProxy(employer)
    }

    /** 供代理层解析宿主；多布局场景下与 [pw.xiaohaozi.xadapter.smart.provider.XProvider] 共用同一 Adapter。 */
    override fun getEmployerAdapter(): SmartAdapter<VB, D> {
        return this
    }


    /**
     * 多布局切换
     * @param isFixed 是否填充整行，仅线性布局、网格布局、瀑布流布局有效。
     * @param itemType
     * @param init 创建Provider后回调，可在此处对Provider做一些初始化操作
     * @param created 创建ViewHolder完成后回调，可在此处对viewHolder做一些初始化工作
     * @param bind 核心方法，UI绑定数据时回调
     *
     * @return 返回Provider
     */
    inline fun <reified pvb : VB, reified pd : D> withType(
        isFixed: Boolean? = null,
        itemType: Int? = null,
        init: (SmartProvider<VB, D, pvb, pd>.() -> Unit) = {},
        crossinline created: OnProviderCreatedHolder<VB, D, pvb, pd> = {},
        crossinline bind: OnProviderBindHolder<VB, D, pvb, pd>,
    ): SmartProvider<VB, D, pvb, pd> {
        val provider = object : SmartProvider<VB, D, pvb, pd>(this) {

            override fun onCreated(holder: XHolder<pvb>) {
                created.invoke(this, holder)
            }

            override fun onBind(scope: CoroutineScope, holder: XHolder<pvb>, data: pd, position: Int) {
            }

            override fun onBind(scope: CoroutineScope, holder: XHolder<pvb>, data: pd, position: Int, payloads: List<Any?>) {
                bind.invoke(this, OnBindParams(holder, data, position, payloads, scope))
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
