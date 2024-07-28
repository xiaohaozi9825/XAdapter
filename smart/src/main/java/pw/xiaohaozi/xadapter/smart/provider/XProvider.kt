package pw.xiaohaozi.xadapter.smart.provider

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.impl.SmartProviderSelectedImpl
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy
import pw.xiaohaozi.xadapter.smart.proxy.SelectedProxy

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 22:08
 */
abstract class XProvider<VB : ViewBinding, D>(
    override val adapter: SmartAdapter<*, *>, //
    private val listener: EventImpl<XProvider<VB, D>, VB, D> = EventImpl(),//
    private val selected: SmartProviderSelectedImpl<XProvider<VB, D>, VB, D> = SmartProviderSelectedImpl()//
) : SmartProvider<VB, D>(adapter), EventProxy<XProvider<VB, D>, VB, D> by listener,//
    SelectedProxy<XProvider<VB, D>, VB, D> by selected //
{
    init {
        initProxy()
    }

    override var employer: XProvider<VB, D>
        get() = this
        set(value) {}

    override fun initProxy(employer: XProvider<VB, D>) {
        listener.initProxy(employer)
        selected.initProxy(employer)
    }

    private fun initProxy() {
        initProxy(this)
    }

    override fun isFixedViewType() = false


}