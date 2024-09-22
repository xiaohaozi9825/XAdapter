package pw.xiaohaozi.xadapter.smart.ext

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.holder.SmartHolder
import pw.xiaohaozi.xadapter.smart.provider.XProvider


/*****************************************************
 * 创建Adapter
 *****************************************************/

/**
 * 创建单布局Adapter
 */
inline fun <VB : ViewBinding, D> createAdapter(
    crossinline init: (XAdapter<VB, D>.(holder: SmartHolder<VB>) -> Unit) = {},
    crossinline bind: XAdapter<VB, D>.(holder: SmartHolder<VB>, data: D, position: Int) -> Unit,
): XAdapter<VB, D> {
    val xAdapter = XAdapter<VB, D>()
    val provider = object : XProvider<VB, D>(xAdapter) {

        override fun onCreated(holder: SmartHolder<VB>) {
            init.invoke(xAdapter, holder)
        }

        override fun onBind(holder: SmartHolder<VB>, data: D, position: Int) {
            bind.invoke(xAdapter, holder, data, position)
        }
    }
    xAdapter.addProvider(provider, 0)
    return xAdapter
}

/**
 * 创建通用Adapter，单布局和多布局都可以使用，建议创建多布局时使用。
 * 后续需要使用.withType()方法实现数据绑定； 最后调用toAdapter()方法还原回Adapter。
 *
 * @param custom 动态生成 itemType
 */
fun createAdapter(custom: (XAdapter<ViewBinding, Any?>.(data: Any?, position: Int) -> Int?)? = null): XAdapter<ViewBinding, Any?> {
    val xAdapter = XAdapter<ViewBinding, Any?>()
    if (custom != null) {
        xAdapter.customItemType { data, position ->
            return@customItemType custom.invoke(xAdapter, data, position)
        }
    }
    return xAdapter
}

/**
 * 多布局切换
 * 返回Provider
 */
inline fun <VB : ViewBinding, D : Any?> XAdapter<ViewBinding, Any?>.withType(
    isFixed: Boolean? = null,
    itemType: Int? = null,
    crossinline init: (XProvider<VB, D>.(holder: SmartHolder<VB>) -> Unit) = {},
    crossinline bind: XProvider<VB, D>.(holder: SmartHolder<VB>, data: D, position: Int) -> Unit,
): XProvider<VB, D> {
    val provider = object : XProvider<VB, D>(this) {

        override fun onCreated(holder: SmartHolder<VB>) {
            init.invoke(this, holder)
        }

        override fun onBind(holder: SmartHolder<VB>, data: D, position: Int) {
            bind.invoke(this, holder, data, position)
        }

        override fun isFixedViewType(): Boolean {
            return isFixed ?: false
        }

    }
    this.addProvider(provider, itemType)
    return provider
}
/**
 * 多布局切换
 * 返回Provider
 */
inline fun <reified VB : ViewBinding, D : Any?> XProvider<out ViewBinding, out Any?>.withType(
    isFixed: Boolean? = null,
    itemType: Int? = null,
    crossinline init: XProvider<VB, D>.(holder: SmartHolder<VB>) -> Unit = {},
    crossinline bind: XProvider<VB, D>.(holder: SmartHolder<VB>, data: D, position: Int) -> Unit,
): XProvider<VB, D> {
    val provider = object : XProvider<VB, D>(adapter) {

        override fun onCreated(holder: SmartHolder<VB>) {
            init.invoke(this, holder)
        }

        override fun onBind(holder: SmartHolder<VB>, data: D, position: Int) {
            bind.invoke(this, holder, data, position)
        }

        override fun isFixedViewType(): Boolean {
            return isFixed ?: false
        }
    }
    adapter.addProvider(provider, itemType)
    return provider
}

/**
 * Provider切换为Adapter
 */
fun <VB : ViewBinding, D> XProvider<VB, D>.toAdapter(): XAdapter<ViewBinding, Any?> {
    return this.adapter as XAdapter<ViewBinding, Any?>
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