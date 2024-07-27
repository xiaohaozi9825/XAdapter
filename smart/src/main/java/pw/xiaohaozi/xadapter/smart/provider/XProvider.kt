package pw.xiaohaozi.xadapter.smart.provider

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.SmartHolder
import pw.xiaohaozi.xadapter.smart.impl.ListenerImpl
import pw.xiaohaozi.xadapter.smart.proxy.ListenerProxy

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 22:08
 */
abstract class XProvider<VB : ViewBinding, D>(override val adapter: SmartAdapter<*, *>, val listener: ListenerImpl<XProvider<VB, D>, VB, D> = ListenerImpl()) : SmartProvider<VB, D>(
    adapter
), ListenerProxy<XProvider<VB, D>, VB, D> by listener {
    init {
        listener.init(this)
    }

    override fun onCreated(holder: SmartHolder<VB>) {
    }


    override fun isFixedViewType() = false

}