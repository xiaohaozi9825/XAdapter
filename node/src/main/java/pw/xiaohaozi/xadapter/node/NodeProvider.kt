package pw.xiaohaozi.xadapter.node

import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.OnProviderBindHolder
import pw.xiaohaozi.xadapter.node.ext.OnProviderInitHolder
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.params.OnBindParams
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.provider.XProvider
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy

/**
 * Node 模块中的列表项 Provider：在 [NodeAdapter] 上注册子类型布局与数据，并支持嵌套 [withType]。
 *
 * 描述：继承 [XProvider] 与 [EventProxy]，事件作用域为当前 Provider。
 * 作者：小耗子
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 22:08
 */
abstract class NodeProvider<AVB : ViewBinding, AD : NodeEntity<*, *>, VB : AVB, D : AD>(
    final override val adapter: NodeAdapter<AVB, AD>,
    private val listener: EventImpl<NodeProvider<AVB, AD, VB, D>, VB, D> = EventImpl(),//
) : XProvider<VB, D>(adapter), EventProxy<NodeProvider<AVB, AD, VB, D>, VB, D> by listener {
    init {
        initProxy()
    }

    override var employer: NodeProvider<AVB, AD, VB, D>
        get() = this
        set(value) {
            throw XAdapterException("employer禁止赋值")
        }

    /**
     * 将事件代理绑定到当前 Provider（构造时调用）。
     */
    override fun initProxy(employer: NodeProvider<AVB, AD, VB, D>) {
        listener.initProxy(employer)
    }

    /** 构造链中初始化 [employer]。 */
    private fun initProxy() {
        initProxy(this)
    }

    override fun isFixedViewType() = false


    /**
     * 多布局切换：注册一种子 [ViewBinding] + 子节点数据类型 [d] 的 Provider。
     * @param isFixed 是否占满整行。
     * @param itemType 显式 itemType；为 null 时由框架自动分配。
     * @param init 创建子 Provider 后的初始化。
     * @param create [XHolder] 创建完成回调。
     * @param bind 数据绑定（含 payloads 时走 [OnBindParams]）。
     * @return 新注册的子 Provider。
     */
    inline fun <reified vb : AVB, reified d : AD> withType(
        isFixed: Boolean? = null,
        itemType: Int? = null,
        crossinline init: (NodeProvider<AVB, AD, vb, d>.() -> Unit) = {},
        crossinline create: OnProviderInitHolder<AVB, AD, vb, d> = {},
        crossinline bind: OnProviderBindHolder<AVB, AD, vb, d>,
    ): NodeProvider<AVB, AD, vb, d> {
        val provider = object : NodeProvider<AVB, AD, vb, d>(adapter) {

            init {
                setExplicitTypes(vb::class.java, d::class.java)
            }

            override fun onCreated(holder: XHolder<vb>) {
                create.invoke(this, holder)
            }

            override fun onBind(scope: CoroutineScope, holder: XHolder<vb>, data: d, position: Int) {

            }

            override fun onBind(scope: CoroutineScope, holder: XHolder<vb>, data: d, position: Int, payloads: List<Any?>) {
                bind.invoke(this, OnBindParams(holder, data, position, payloads, scope))
            }

            override fun isFixedViewType(): Boolean {
                return isFixed ?: false
            }
        }
        adapter.addProvider(provider, itemType)
        init.invoke(provider)
        return provider
    }


    /** 从嵌套 Provider 回到外层 [NodeAdapter]。 */
    fun toAdapter(): NodeAdapter<AVB, AD> {
        return this.adapter
    }

}
