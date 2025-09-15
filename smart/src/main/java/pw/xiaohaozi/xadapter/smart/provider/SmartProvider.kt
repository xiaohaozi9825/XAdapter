package pw.xiaohaozi.xadapter.smart.provider

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.ext.OnBindParams
import pw.xiaohaozi.xadapter.smart.ext.OnProviderBindHolder
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy
import pw.xiaohaozi.xadapter.smart.ext.OnProviderInitHolder as OnProviderInitHolder1

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 22:08
 */
abstract class SmartProvider<AVB : ViewBinding, AD, VB : ViewBinding, D>(
    override val adapter: SmartAdapter<AVB, AD>, //
    private val listener: EventImpl<SmartProvider<AVB, AD, VB, D>, VB, D> = EventImpl(),//
) : XProvider<VB, D>(adapter), EventProxy<SmartProvider<AVB, AD, VB, D>, VB, D> by listener {
    init {
        initProxy()
    }


    override var employer: SmartProvider<AVB, AD, VB, D>
        get() = this
        set(value) {}

    override fun initProxy(employer: SmartProvider<AVB, AD, VB, D>) {
        listener.initProxy(employer)
    }

    private fun initProxy() {
        initProxy(this)
    }

    override fun isFixedViewType() = false


    fun getSmartAdapter(): SmartAdapter<ViewBinding, Any?> {
        return adapter as SmartAdapter<ViewBinding, Any?>
    }

    /**
     * 多布局切换
     * 返回Provider
     */
    inline fun <PVB : AVB, PD : AD> withType(
        isFixed: Boolean? = null,
        itemType: Int? = null,
        crossinline init: (SmartProvider<AVB, AD, PVB, PD>.() -> Unit) = {},
        crossinline create: OnProviderInitHolder1<AVB, AD, PVB, PD> = {},
        crossinline bind: OnProviderBindHolder<AVB, AD, PVB, PD>,
    ): SmartProvider<AVB, AD, PVB, PD> {
        val provider = object : SmartProvider<AVB, AD, PVB, PD>(adapter) {

            override fun onCreated(holder: XHolder<PVB>) {
                create.invoke(this, holder)
            }

            override fun onBind(holder: XHolder<PVB>, data: PD, position: Int) {
            }

            override fun onBind(holder: XHolder<PVB>, data: PD, position: Int, payloads: List<Any?>) {
                bind.invoke(this, OnBindParams(holder, data, position, payloads))
            }

            override fun isFixedViewType(): Boolean {
                return isFixed ?: false
            }

        }
        this.getSmartAdapter().addProvider(provider, itemType)
        init.invoke(provider)
        return provider
    }

    fun toAdapter(): SmartAdapter<AVB, AD> {
        return adapter
    }
}