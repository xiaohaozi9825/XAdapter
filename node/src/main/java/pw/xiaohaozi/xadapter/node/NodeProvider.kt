package pw.xiaohaozi.xadapter.node

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.provider.XProvider
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 22:08
 */
abstract class NodeProvider<VB : ViewBinding, D : NodeEntity<*, *>>(
    private val _adapter: NodeAdapter<*, *>,
    private val listener: EventImpl<NodeProvider<VB, D>, VB, D> = EventImpl(),//
) : XProvider<VB, D>(_adapter), EventProxy<NodeProvider<VB, D>, VB, D> by listener {
    init {
        initProxy()
    }

    override val adapter: NodeAdapter<ViewBinding, NodeEntity<*, *>>
        get() = (_adapter as NodeAdapter<ViewBinding, NodeEntity<*, *>>)


    override var employer: NodeProvider<VB, D>
        get() = this
        set(value) {}

    override fun initProxy(employer: NodeProvider<VB, D>) {
        listener.initProxy(employer)
    }

    private fun initProxy() {
        initProxy(this)
    }

    override fun isFixedViewType() = false

}