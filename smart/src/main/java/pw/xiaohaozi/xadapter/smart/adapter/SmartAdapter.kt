package pw.xiaohaozi.xadapter.smart.adapter

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.entity.DEFAULT_PAGE
import pw.xiaohaozi.xadapter.smart.entity.EMPTY
import pw.xiaohaozi.xadapter.smart.entity.FOOTER
import pw.xiaohaozi.xadapter.smart.entity.HEADER
import pw.xiaohaozi.xadapter.smart.ext.OnBindParams
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
) : XAdapter<VB, D, SmartAdapter<VB, D>>(),//继承Adapter
    XEmployer, //宿主
    SmartDataProxy<SmartAdapter<VB, D>, VB, D> by dataProxy,//数据操作
    EventProxy<SmartAdapter<VB, D>, VB, D> by eventProxy,//时间监听
    SelectedProxy<SmartAdapter<VB, D>, VB, D> by selectedProxy //选择操作
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

    override fun getEmployerAdapter(): SmartAdapter<VB, D> {
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
                bind.invoke(this, OnBindParams(holder, data, position, payloads))
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