package pw.xiaohaozi.xadapter.smart.provider

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.ext.OnBindParams
import pw.xiaohaozi.xadapter.smart.ext.OnProviderBindHolder
import pw.xiaohaozi.xadapter.smart.ext.OnProviderCreatedHolder
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 22:08
 */
abstract class SmartProvider<AVB : ViewBinding, AD, PVB : ViewBinding, PD>(
    override val adapter: SmartAdapter<AVB, AD>, //
    private val listener: EventImpl<SmartProvider<AVB, AD, PVB, PD>, PVB, PD> = EventImpl(),//
) : XProvider<PVB, PD>(adapter), EventProxy<SmartProvider<AVB, AD, PVB, PD>, PVB, PD> by listener {
    init {
        initProxy()
    }


    override var employer: SmartProvider<AVB, AD, PVB, PD>
        get() = this
        set(value) {}

    override fun initProxy(employer: SmartProvider<AVB, AD, PVB, PD>) {
        listener.initProxy(employer)
    }

    private fun initProxy() {
        initProxy(this)
    }

    override fun isFixedViewType() = false


    fun getSmartAdapter(): SmartAdapter<AVB, AD> {
        return adapter
    }

    /**
     * 多布局切换
     * 返回Provider
     */
    inline fun <reified pvb : AVB, reified pd : AD> withType(
        isFixed: Boolean? = null,
        itemType: Int? = null,
        init: (SmartProvider<AVB, AD, pvb, pd>.() -> Unit) = {},
        crossinline created: OnProviderCreatedHolder<AVB, AD, pvb, pd> = {},
        crossinline bind: OnProviderBindHolder<AVB, AD, pvb, pd>,
    ): SmartProvider<AVB, AD, pvb, pd> {
        val provider = object : SmartProvider<AVB, AD, pvb, pd>(adapter) {

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
        adapter.addProvider(provider, itemType)
        init.invoke(provider)
        return provider
    }

    fun toAdapter(): SmartAdapter<AVB, AD> {
        return adapter
    }
}