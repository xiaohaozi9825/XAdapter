package pw.xiaohaozi.xadapter.smart.ext

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.dragswipe.DragSort
import pw.xiaohaozi.xadapter.smart.dragswipe.ItemDrag
import pw.xiaohaozi.xadapter.smart.dragswipe.ItemSwipe
import pw.xiaohaozi.xadapter.smart.dragswipe.SwipeDelete
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider
import pw.xiaohaozi.xadapter.smart.widgets.SwipeItemLayout


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
    val provider = object : SmartProvider<VB, D>(adapter) {

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
    crossinline init: (SmartProvider<VB, D>.() -> Unit) = {},
    crossinline create: OnProviderInitHolder<VB, D> = {},
    crossinline bind: OnProviderBindHolder<VB, D>,
): SmartProvider<VB, D> {
    val provider = object : SmartProvider<VB, D>(this) {

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
    crossinline init: (SmartProvider<VB, D>.() -> Unit) = {},
    crossinline create: OnProviderInitHolder<VB, D> = {},
    crossinline bind: OnProviderBindHolder<VB, D>,
): SmartProvider<VB, D> {
    val provider = object : SmartProvider<VB, D>(adapter) {

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

/**
 * 侧滑菜单
 * item 根布局必须使用 SwipeItemLayout
 */
fun <A : SmartAdapter<VB, D>, VB : ViewBinding, D> A.swipeMenu(): A {
    addOnRecyclerViewChanges(object : XAdapter.OnRecyclerViewChanges {
        lateinit var listener: RecyclerView.OnItemTouchListener
        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            listener = SwipeItemLayout.OnSwipeItemTouchListener(recyclerView.context)
            recyclerView.addOnItemTouchListener(listener)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            recyclerView.removeOnItemTouchListener(listener)
        }
    })
    return this
}


/**
 * 侧滑删除
 *
 * @param threshold 设置用户应该移动视图的部分，将其视为已滑动。分数是根据RecyclerView的边界计算的。 如果设置0.5f，这意味着，要滑动视图，用户必须移动视图至少一半的RecyclerView的宽度或高度，这取决于滑动的方向。
 * @param flags 滑动移动方向。默认值为ItemTouchHelper.END or ItemTouchHelper.START。
 * @param start 开始滑动
 * @param end 滑动结束（手指松开，不管有没有触发侧滑事件都会调用）
 * @param swipe 触发侧滑事件，返回是否消费掉该事件
 */
fun <T : SmartAdapter<*, *>> T.swipeDelete(
    threshold: Float = 0.5f,
    flags: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int = { _, _ -> START or END },
    start: ((viewHolder: RecyclerView.ViewHolder?) -> Unit)? = null,
    end: ((recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Unit)? = null,
    swipe: ((viewHolder: RecyclerView.ViewHolder, direction: Int) -> Boolean)? = null,
): T {
    addOnRecyclerViewChanges(object : XAdapter.OnRecyclerViewChanges {
        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            ItemTouchHelper(ItemSwipe(SwipeDelete(threshold, flags, start, end, swipe)))
                .attachToRecyclerView(recyclerView)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {

        }
    })

    return this
}


/**
 * 拖拽排序
 *
 * @param threshold 设置用户在拖拽视图时应该移动视图的比例。在视图移动到这个位置之后，ItemTouchHelper开始检查视图下方是否有可能的删除。一个浮点值，表示视图大小的百分比。缺省值为。1f。
 * @param flags 触发方向
 * @param start 开始拖拽
 * @param end 结束拖拽（松开手就会调用）
 * @param onMove 被拖拽的item多拽到其他item位置上是调用,该参数会替换掉现有的onMove逻辑
 * @param swap 当两个item交换时调用
 */
fun <VB : ViewBinding, D> SmartProvider<VB, D>.swipeDelete(
    threshold: Float = 0.5f,
    flags: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int = { _, holder -> if (holder.itemViewType == getItemViewType()) START or END else 0 },
    start: ((viewHolder: RecyclerView.ViewHolder?) -> Unit)? = null,
    end: ((recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Unit)? = null,
    swipe: ((viewHolder: RecyclerView.ViewHolder, direction: Int) -> Boolean)? = null,
): SmartProvider<VB, D> {
    adapter.addOnRecyclerViewChanges(object : XAdapter.OnRecyclerViewChanges {
        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            ItemTouchHelper(ItemSwipe(SwipeDelete(threshold, flags, start, end, swipe)))
                .attachToRecyclerView(recyclerView)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {

        }
    })
    return this
}

/**
 * 拖拽排序
 *
 * @param threshold 设置用户在拖拽视图时应该移动视图的比例。在视图移动到这个位置之后，ItemTouchHelper开始检查视图下方是否有可能的删除。一个浮点值，表示视图大小的百分比。缺省值为。1f。
 * @param flags 触发方向
 * @param start 开始拖拽
 * @param end 结束拖拽（松开手就会调用）
 * @param onMove 被拖拽的item多拽到其他item位置上是调用,该参数会替换掉现有的onMove逻辑
 * @param swap 当两个item交换时调用
 */
fun <T : SmartAdapter<*, *>> T.dragSort(
    threshold: Float = 0.1f,
    flags: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int = { _, _ -> UP or DOWN or START or END },
    start: ((viewHolder: RecyclerView.ViewHolder?) -> Unit)? = null,
    end: ((recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Unit)? = null,
    onMove: ((
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) -> Boolean)? = null,
    swap: ((
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
        fromPosition: Int,
        toPosition: Int,
    ) -> Unit)? = null,
): T {
    addOnRecyclerViewChanges(object : XAdapter.OnRecyclerViewChanges {
        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            ItemTouchHelper(ItemDrag(DragSort(threshold, flags, start, end, onMove, swap)))
                .attachToRecyclerView(recyclerView)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {

        }
    })

    return this
}


/**
 * 拖拽排序
 *
 * @param threshold 设置用户在拖拽视图时应该移动视图的比例。在视图移动到这个位置之后，ItemTouchHelper开始检查视图下方是否有可能的删除。一个浮点值，表示视图大小的百分比。缺省值为。1f。
 * @param flags 触发方向
 * @param start 开始拖拽
 * @param end 结束拖拽（松开手就会调用）
 * @param onMove 被拖拽的item多拽到其他item位置上是调用,该参数会替换掉现有的onMove逻辑
 * @param swap 当两个item交换时调用
 */
fun <VB : ViewBinding, D> SmartProvider<VB, D>.dragSort(
    threshold: Float = 0.1f,
    flags: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int = { _, holder -> if (holder.itemViewType == getItemViewType()) ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END else 0 },
    start: ((viewHolder: RecyclerView.ViewHolder?) -> Unit)? = null,
    end: ((recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Unit)? = null,
    onMove: ((
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) -> Boolean)? = null,
    swap: ((
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
        fromPosition: Int,
        toPosition: Int,
    ) -> Unit)? = null,
): SmartProvider<VB, D> {
    adapter.addOnRecyclerViewChanges(object : XAdapter.OnRecyclerViewChanges {
        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            ItemTouchHelper(ItemDrag(DragSort(threshold, flags, start, end, onMove, swap)))
                .attachToRecyclerView(recyclerView)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {

        }
    })
    return this
}


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