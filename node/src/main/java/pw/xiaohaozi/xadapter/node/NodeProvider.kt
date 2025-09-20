package pw.xiaohaozi.xadapter.node

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.OnProviderBindHolder
import pw.xiaohaozi.xadapter.node.ext.OnProviderInitHolder
import pw.xiaohaozi.xadapter.smart.ext.OnBindParams
import pw.xiaohaozi.xadapter.smart.holder.XHolder
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
abstract class NodeProvider<AVB : ViewBinding, AD : NodeEntity<*, *>, VB : AVB, D : AD>(
    override val adapter: NodeAdapter<AVB, AD>,
    private val listener: EventImpl<NodeProvider<AVB, AD, VB, D>, VB, D> = EventImpl(),//
) : XProvider<VB, D>(adapter), EventProxy<NodeProvider<AVB, AD, VB, D>, VB, D> by listener {
    init {
        initProxy()
    }

    override var employer: NodeProvider<AVB, AD, VB, D>
        get() = this
        set(value) {}

    override fun initProxy(employer: NodeProvider<AVB, AD, VB, D>) {
        listener.initProxy(employer)
    }

    private fun initProxy() {
        initProxy(this)
    }

    override fun isFixedViewType() = false


    /**
     * 多布局切换
     * 返回Provider
     */
    inline fun <reified vb : AVB, reified d : AD> withType(
        isFixed: Boolean? = null,
        itemType: Int? = null,
        crossinline init: (NodeProvider<AVB, AD, vb, d>.() -> Unit) = {},
        crossinline create: OnProviderInitHolder<AVB, AD, vb, d> = {},
        crossinline bind: OnProviderBindHolder<AVB, AD, vb, d>,
    ): NodeProvider<AVB, AD, vb, d> {
        val provider = object : NodeProvider<AVB, AD, vb, d>(adapter) {

            override fun onCreated(holder: XHolder<vb>) {
                create.invoke(this, holder)
            }

            override fun onBind(holder: XHolder<vb>, data: d, position: Int) {

            }

            override fun onBind(holder: XHolder<vb>, data: d, position: Int, payloads: List<Any?>) {
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


    /**
     * Provider切换为Adapter
     */
    fun toAdapter(): NodeAdapter<AVB, AD> {
        return this.adapter
    }

}