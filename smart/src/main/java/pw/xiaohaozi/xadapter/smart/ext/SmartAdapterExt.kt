package pw.xiaohaozi.xadapter.smart.ext

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider


/*****************************************************
 * 创建Adapter
 *****************************************************/
typealias OnAdapterInitHolder<VB, D> = SmartAdapter<VB, D>.(holder: XHolder<VB>) -> Unit
typealias OnProviderInitHolder<VB, D> = SmartProvider<VB, D>.(holder: XHolder<VB>) -> Unit
typealias OnAdapterBindHolder<VB, D> = SmartAdapter<VB, D>.(params: OnBindParams<VB, D>) -> Unit
typealias OnProviderBindHolder<VB, D> = SmartProvider<VB, D>.(params: OnBindParams<VB, D>) -> Unit
typealias OnCustomType = (SmartAdapter<ViewBinding, Any?>.(data: Any?, position: Int) -> Int?)

data class OnBindParams<VB : ViewBinding, D>(
    val holder: XHolder<VB>,
    val data: D,
    val position: Int,
    val payloads: List<Any?>
)


/**
 * 创建单布局Adapter
 */
inline fun <VB : ViewBinding, D> createAdapter(
    crossinline init: (SmartProvider<VB, D>.() -> Unit) = {},
    crossinline create: OnAdapterInitHolder<VB, D> = {},
    crossinline bind: OnAdapterBindHolder<VB, D>,
): SmartAdapter<VB, D> {
    val adapter = SmartAdapter<VB, D>()
    val provider = object : SmartProvider<VB, D>(adapter, select = true) {

        override fun onCreated(holder: XHolder<VB>) {
            create.invoke(adapter, holder)
        }

        override fun onBind(holder: XHolder<VB>, data: D, position: Int) {
        }

        override fun onBind(holder: XHolder<VB>, data: D, position: Int, payloads: List<Any?>) {
            bind.invoke(adapter, OnBindParams(holder, data, position, payloads))
        }
    }
    adapter.addProvider(provider, 0)
    init.invoke(provider)
    return adapter
}

/**
 * 创建通用Adapter，单布局和多布局都可以使用，建议创建多布局时使用。
 * 后续需要使用.withType()方法实现数据绑定； 最后调用toAdapter()方法还原回Adapter。
 *
 * @param custom 动态生成 itemType
 */
fun createAdapter(custom: OnCustomType? = null): SmartAdapter<ViewBinding, Any?> {
    val adapter = SmartAdapter<ViewBinding, Any?>()
    if (custom != null) {
        adapter.customItemType { data, position ->
            return@customItemType custom.invoke(adapter, data, position)
        }
    }
    return adapter
}


/**
 * 多布局切换
 * 返回Provider
 */
inline fun <VB : ViewBinding, D : Any?> SmartAdapter<ViewBinding, Any?>.withType(
    isFixed: Boolean? = null,
    itemType: Int? = null,
    select: Boolean = false,
    crossinline init: (SmartProvider<VB, D>.() -> Unit) = {},
    crossinline create: OnProviderInitHolder<VB, D> = {},
    crossinline bind: OnProviderBindHolder<VB, D>,
): SmartProvider<VB, D> {
    val provider = object : SmartProvider<VB, D>(this, select = select) {

        override fun onCreated(holder: XHolder<VB>) {
            create.invoke(this, holder)
        }

        override fun onBind(holder: XHolder<VB>, data: D, position: Int) {
        }

        override fun onBind(holder: XHolder<VB>, data: D, position: Int, payloads: List<Any?>) {
            bind.invoke(this, OnBindParams(holder, data, position, payloads))
        }

        override fun isFixedViewType(): Boolean {
            return isFixed ?: false
        }

    }
    this.addProvider(provider, itemType)
    init.invoke(provider)
    return provider
}


/**
 * 多布局切换
 * 返回Provider
 */
inline fun <reified VB : ViewBinding, D : Any?> SmartProvider<out ViewBinding, out Any?>.withType(
    isFixed: Boolean? = null,
    itemType: Int? = null,
    select: Boolean = false,
    crossinline init: (SmartProvider<VB, D>.() -> Unit) = {},
    crossinline create: OnProviderInitHolder<VB, D> = {},
    crossinline bind: OnProviderBindHolder<VB, D>,
): SmartProvider<VB, D> {
    val provider = object : SmartProvider<VB, D>(adapter, select = select) {

        override fun onCreated(holder: XHolder<VB>) {
            create.invoke(this, holder)
        }

        override fun onBind(holder: XHolder<VB>, data: D, position: Int) {

        }

        override fun onBind(holder: XHolder<VB>, data: D, position: Int, payloads: List<Any?>) {
            bind.invoke(this, OnBindParams(holder, data, position, payloads))
//            bind.invoke(this, OnBind(holder, data, position, payloads))
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
fun <VB : ViewBinding, D> SmartProvider<VB, D>.toAdapter(): SmartAdapter<ViewBinding, Any?> {
    return this.adapter as SmartAdapter<ViewBinding, Any?>
}


/*****************************************************
 * 其他操作
 *****************************************************/
//
///**
// * 设置为单选
// * 此方法不会触发点击事件，需要手动编写点击事件与选择事件
// */
//fun <A : SmartAdapter<VB, D>, VB : ViewBinding, D> A.radio(): A {
//    this.setMaxSelectedCount(1)
//        .allowCancel(false)
//    return this
//}
//
///**
// * 设置为单选
// * 此方法会触发点击事件，改方法会设置选择事件，并回调回来
// */
//fun <A : SmartAdapter<VB, D>, VB : ViewBinding, D> A.radio(
//    @IdRes id: Int? = null,
//    listener: A.(holder: SmartHolder<VB>?, data: D?, position: Int, index: Int, view: View?) -> Unit
//): A {
//    this.setMaxSelectedCount(1)
//        .allowCancel(false)
//        .setOnSelectedListener(id) { holder, data, position, index, view ->
//            listener.invoke(this@radio, holder, data, position, index, view)
//        }
//    return this
//}