package pw.xiaohaozi.xadapter.smart.provider

import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import pw.xiaohaozi.xadapter.smart.utils.applyExplicitTypes
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.entity.XMultiItemEntity
import pw.xiaohaozi.xadapter.smart.params.OnBindParams
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.ext.OnProviderBindHolder
import pw.xiaohaozi.xadapter.smart.ext.OnProviderCreatedHolder
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy

/**
 * 多类型列表中的单一「布局 + 数据」提供者，继承 [XProvider] 并混入 [EventProxy]，可在 Provider 作用域内注册点击等事件。
 *
 * 描述：与 [SmartAdapter] 配合使用；子布局可通过 [withType] 嵌套注册。
 * 作者：小耗子
 * 创建时间：2024/6/9 22:08
 */
abstract class SmartProvider<AVB : ViewBinding, AD, PVB : ViewBinding, PD>(
    final override val adapter: SmartAdapter<AVB, AD>, //
    private val listener: EventImpl<SmartProvider<AVB, AD, PVB, PD>, PVB, PD> = EventImpl(),//
) : XProvider<PVB, PD>(adapter), EventProxy<SmartProvider<AVB, AD, PVB, PD>, PVB, PD> by listener {
    init {
        initProxy()
    }


    override var employer: SmartProvider<AVB, AD, PVB, PD>
        get() = this
        set(value) {
            throw XAdapterException("employer禁止赋值")
        }

    override fun initProxy(employer: SmartProvider<AVB, AD, PVB, PD>) {
        listener.initProxy(employer)
    }

    private fun initProxy() {
        initProxy(this)
    }

    override fun isFixedViewType() = false


    /** 当前 Provider 所属的 [SmartAdapter]。 */
    fun getSmartAdapter(): SmartAdapter<AVB, AD> {
        return adapter
    }

    /**
     * 多布局切换：注册一种子 [ViewBinding] + 子数据类型 [pd] 的 Provider。
     * @param isFixed 是否占满整行/整列（线性、网格、瀑布流有效）。
     * @param itemType 显式指定 itemType；为 null 时由框架自动分配。
     * @param init 创建 Provider 后的初始化（如事件、拖拽扩展）。
     * @param created [XHolder] 创建完成后的回调。
     * @param bind 绑定数据；优先走带 [payloads] 的重载（通过 [OnBindParams]）。
     * @return 新注册的子 Provider。
     */
    inline fun <reified pvb : AVB, reified pd : AD> withType(
        isFixed: Boolean? = null,
        itemType: Int? = null,
        init: (SmartProvider<AVB, AD, pvb, pd>.() -> Unit) = {},
        crossinline created: OnProviderCreatedHolder<AVB, AD, pvb, pd> = {},
        crossinline bind: OnProviderBindHolder<AVB, AD, pvb, pd>,
    ): SmartProvider<AVB, AD, pvb, pd> {
        if (itemType == null && XMultiItemEntity::class.java.isAssignableFrom(pd::class.java)) {
            throw XAdapterException("provider 的数据类型实现了 XMultiItemEntity，withType() 的 itemType 不能为空")
        }
        val provider = object : SmartProvider<AVB, AD, pvb, pd>(adapter) {

            init {
                applyExplicitTypes(pvb::class.java, pd::class.java)
            }

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
        adapter.addProvider(provider, itemType)
        init.invoke(provider)
        return provider
    }

    /** 从嵌套的 Provider 链回到外层 [SmartAdapter]，便于继续配置 Adapter 级能力。 */
    fun toAdapter(): SmartAdapter<AVB, AD> {
        return adapter
    }
}
