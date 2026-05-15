package pw.xiaohaozi.xadapter.smart.ext

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_ID
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import pw.xiaohaozi.xadapter.smart.params.OnBindParams
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
typealias OnProviderCreatedHolder<AVB, AD, VB, D> = SmartProvider<AVB, AD, VB, D>.(holder: XHolder<VB>) -> Unit
typealias OnAdapterBindHolder<VB, D> = SmartAdapter<VB, D>.(params: OnBindParams<VB, D>) -> Unit
typealias OnProviderBindHolder<AVB, AD, VB, D> = SmartProvider<AVB, AD, VB, D>.(params: OnBindParams<VB, D>) -> Unit
typealias OnCustomType<VB, D> = (SmartAdapter<VB, D>.(data: D, position: Int) -> Int?)
typealias OnItemId<VB, D> = (SmartAdapter<VB, D>.(position: Int) -> Long)




/**
 * 创建单布局Adapter
 * 泛型VB ：布局文件，泛型D：数据
 * @param itemType
 * @param onItemId
 * @param init 创建Provider后回调，可在此处对Provider做一些初始化操作
 * @param created 创建ViewHolder完成后回调，可在此处对viewHolder做一些初始化工作
 * @param bind 核心方法，UI绑定数据时回调
 *
 * @return 返回创建好的adapter
 */
inline fun <reified VB : ViewBinding, reified D> createAdapter(
    itemType: Int = 0,
    crossinline onItemId: OnItemId<VB, D> = { NO_ID },
    crossinline init: (SmartProvider<VB, D, VB, D>.() -> Unit) = {},
    crossinline created: OnAdapterInitHolder<VB, D> = {},
    crossinline bind: OnAdapterBindHolder<VB, D>,
): SmartAdapter<VB, D> {
    val adapter = object : SmartAdapter<VB, D>() {
        override fun getItemId(position: Int): Long {
            return onItemId.invoke(this, position)
        }
    }
    val provider = object : SmartProvider<VB, D, VB, D>(adapter) {

        override fun onCreated(holder: XHolder<VB>) {
            created.invoke(adapter, holder)
        }

        override fun onBind(scope: CoroutineScope, holder: XHolder<VB>, data: D, position: Int) {
        }

        override fun onBind(scope: CoroutineScope, holder: XHolder<VB>, data: D, position: Int, payloads: List<Any?>) {
            bind.invoke(adapter, OnBindParams(holder, data, position, payloads, scope))
        }
    }
    adapter.addProvider(provider, itemType)
    init.invoke(provider)
    return adapter
}


/**
 * 创建单布局Adapter
 */
inline fun <reified VB : ViewBinding, reified D> LifecycleOwner.createLifecycleAdapter(
    itemType: Int = 0,
    crossinline onItemId: OnItemId<VB, D> = { NO_ID },
    crossinline init: (SmartProvider<VB, D, VB, D>.() -> Unit) = {},
    crossinline create: OnAdapterInitHolder<VB, D> = {},
    crossinline bind: OnAdapterBindHolder<VB, D>,
): SmartAdapter<VB, D> {
    val adapter = createAdapter<VB, D>(itemType, onItemId, init, create, bind)
    adapter.bindLifecycle(this)
    return adapter
}


/**
 * 创建通用Adapter，单布局和多布局都可以使用，建议创建多布局时使用。
 * 后续需要使用.withType()方法实现数据绑定； 最后调用toAdapter()方法还原回Adapter。
 *
 * @param custom 动态生成 itemType
 */
inline fun <reified VB : ViewBinding, reified D> createTypeAdapter(
    noinline onItemId: OnItemId<VB, D>? = null,
    noinline custom: OnCustomType<VB, D>? = null,
): SmartAdapter<VB, D> {
    val adapter = object : SmartAdapter<VB, D>() {
        override fun getItemId(position: Int): Long {
            return onItemId?.invoke(this, position) ?: super.getItemId(position)
        }
    }
    if (custom != null) {
        adapter.customItemType { data, position ->
            return@customItemType custom.invoke(adapter, data, position)
        }
    }
    return adapter
}

/**
 * 创建多布局Adapter
 * @param onItemId
 * @param custom 根据数据动态返回itemType
 * @return 多类型Adapter，泛型类型必须为<ViewBinding, Any?>
 */
fun createAdapter(
    onItemId: OnItemId<ViewBinding, Any?>? = null,
    custom: OnCustomType<ViewBinding, Any?>? = null,
): SmartAdapter<ViewBinding, Any?> {
    return createTypeAdapter<ViewBinding, Any?>(onItemId, custom)
}

/**
 * 与 [createTypeAdapter] 相同，并在创建后调用 [SmartAdapter.bindLifecycle] 绑定 [LifecycleOwner]。
 */
inline fun <reified VB : ViewBinding, reified D> LifecycleOwner.createLifecycleTypeAdapter(
    noinline custom: OnCustomType<VB, D>? = null,
    noinline onItemId: OnItemId<VB, D> = { NO_ID }
): SmartAdapter<VB, D> {
    val adapter = createTypeAdapter<VB, D>(onItemId, custom)
    adapter.bindLifecycle(this)
    return adapter
}

/**
 * 创建「任意 ViewBinding + Any?」多类型 Adapter，并绑定当前 [LifecycleOwner] 生命周期。
 */
fun LifecycleOwner.createLifecycleAdapter(
    custom: OnCustomType<ViewBinding, Any?>? = null,
    onItemId: OnItemId<ViewBinding, Any?> = { NO_ID }
): SmartAdapter<ViewBinding, Any?> {
    val adapter = createAdapter(onItemId, custom)
    adapter.bindLifecycle(this)
    return adapter
}

/**
 * setOnClickListener 的简易写法
 */
fun <VB : ViewBinding, D> SmartAdapter<VB, D>.onClick(
    id: Int? = null, listener: (biding: VB, data: D, position: Int, view: View) -> Unit
): SmartAdapter<VB, D> {
    setOnClickListener(id) { holder, data, position, view ->
        listener.invoke(holder.binding, data, position, view)
    }
    return this
}

/**
 * setOnClickListener 的简易写法
 */
fun <AVB : ViewBinding, AD, PVB : ViewBinding, PD> SmartProvider<AVB, AD, PVB, PD>.onClick(
    id: Int? = null, listener: (biding: PVB, data: PD, position: Int, view: View) -> Unit
): SmartProvider<AVB, AD, PVB, PD> {
    setOnClickListener(id) { holder, data, position, view ->
        listener.invoke(holder.binding, data, position, view)
    }
    return this
}

/**
 * setOnLongClickListener 的简易写法
 */
fun <VB : ViewBinding, D> SmartAdapter<VB, D>.onLongClick(
    id: Int? = null, listener: (biding: VB, data: D, position: Int, view: View) -> Unit
): SmartAdapter<VB, D> {
    setOnLongClickListener(id) { holder, data, position, view ->
        listener.invoke(holder.binding, data, position, view)
        return@setOnLongClickListener false
    }
    return this
}

/**
 * setOnLongClickListener 的简易写法
 */
fun <AVB : ViewBinding, AD, PVB : ViewBinding, PD> SmartProvider<AVB, AD, PVB, PD>.onLongClick(
    id: Int? = null, listener: (biding: PVB, data: PD, position: Int, view: View) -> Unit
): SmartProvider<AVB, AD, PVB, PD> {
    setOnLongClickListener(id) { holder, data, position, view ->
        listener.invoke(holder.binding, data, position, view)
        return@setOnLongClickListener false
    }
    return this
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
 * 侧滑删除
 *
 * @param threshold 设置用户在拖拽视图时应该移动视图的比例。在视图移动到这个位置之后，ItemTouchHelper开始检查视图下方是否有可能的删除。一个浮点值，表示视图大小的百分比。缺省值为。1f。
 * @param flags 触发方向
 * @param start 开始拖拽
 * @param end 结束拖拽（松开手就会调用）
 * @param onMove 被拖拽的item多拽到其他item位置上是调用,该参数会替换掉现有的onMove逻辑
 * @param swap 当两个item交换时调用
 */
fun <AVB : ViewBinding, AD, PVB : ViewBinding, PD> SmartProvider<AVB, AD, PVB, PD>.swipeDelete(
    threshold: Float = 0.5f,
    flags: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int = { _, holder -> if (holder.itemViewType == getItemViewType()) START or END else 0 },
    start: ((viewHolder: RecyclerView.ViewHolder?) -> Unit)? = null,
    end: ((recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Unit)? = null,
    swipe: ((viewHolder: RecyclerView.ViewHolder, direction: Int) -> Boolean)? = null,
): SmartProvider<AVB, AD, PVB, PD> {
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
fun <AVB : ViewBinding, AD, PVB : ViewBinding, PD> SmartProvider<AVB, AD, PVB, PD>.dragSort(
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
): SmartProvider<AVB, AD, PVB, PD> {
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


/**
 * 设置为单选
 * 此方法会触发点击事件，改方法会设置选择事件，并回调回来
 */
fun <A : SmartAdapter<VB, D>, VB : ViewBinding, D> A.singleSelect(
    id: Int? = null,
    payload: Any? = null,
    listener: A.(data: D, position: Int, index: Int, fromUser: Boolean) -> Unit
): A {
    this.setMaxSelectCount(1)
        .isAllowCancel(false)
        .setOnItemSelectListener(id, payload) { data, position, index, fromUser ->
            listener.invoke(this@singleSelect, data, position, index, fromUser)
        }
    return this
}

/**
 * 单选封装（限定参与选择的 [itemType]）；内部设置最大可选 1 且默认不允许点击取消。
 * @param permittedTypes 允许参与选择的 itemType 数组
 */
fun <A : SmartAdapter<VB, D>, VB : ViewBinding, D> A.singleSelect(
    id: Int? = null,
    payload: Any? = null,
    permittedTypes: Array<Int>,
    listener: A.(data: D, position: Int, index: Int, fromUser: Boolean) -> Unit
): A {
    this.setMaxSelectCount(1)
        .isAllowCancel(false)
        .setOnItemSelectListener(id, payload, permittedTypes) { data, position, index, fromUser ->
            listener.invoke(this@singleSelect, data, position, index, fromUser)
        }
    return this
}

/**
 * 单选封装（按数据类型 [Class] 限定参与选择的 Provider）；内部设置最大可选 1 且默认不允许点击取消。
 * @param permittedTypes 允许参与选择的数据类型（需与 Provider 泛型一致）
 */
fun <A : SmartAdapter<VB, D>, VB : ViewBinding, D> A.singleSelect(
    id: Int? = null,
    payload: Any? = null,
    permittedTypes: Array<Class<*>>,
    listener: A.(data: D, position: Int, index: Int, fromUser: Boolean) -> Unit
): A {
    this.setMaxSelectCount(1)
        .isAllowCancel(false)
        .setOnItemSelectListener(id, payload, permittedTypes) { data, position, index, fromUser ->
            listener.invoke(this@singleSelect, data, position, index, fromUser)
        }
    return this
}
