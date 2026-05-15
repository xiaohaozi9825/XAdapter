package pw.xiaohaozi.xadapter.node.dragswipe

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.RecyclerView
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.smart.dragswipe.ItemSwipe
import pw.xiaohaozi.xadapter.smart.dragswipe.SwipeCallback
import pw.xiaohaozi.xadapter.smart.holder.XHolder


/**
 * 侧滑删除
 *
 * @param threshold 设置用户应该移动视图的部分，将其视为已滑动。分数是根据RecyclerView的边界计算的。 如果设置0.5f，这意味着，要滑动视图，用户必须移动视图至少一半的RecyclerView的宽度或高度，这取决于滑动的方向。
 * @param flags 滑动移动方向。默认值为ItemTouchHelper.END or ItemTouchHelper.START。
 * @param start 开始滑动
 * @param end 滑动结束（手指松开，不管有没有触发侧滑事件都会调用）
 * @param swipe 触发侧滑事件，返回是否消费掉该事件
 */
class SwipeDelete(
    private val threshold: Float = 0.5f,
    private val flags: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int = { _, _ -> ItemTouchHelper.START or ItemTouchHelper.END },
    private val start: ((viewHolder: RecyclerView.ViewHolder?) -> Unit)? = null,
    private val end: ((recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Unit)? = null,
    private val swipe: ((viewHolder: RecyclerView.ViewHolder, direction: Int) -> Boolean)? = null,
) : SwipeCallback {

    override lateinit var itemSwipe: ItemSwipe

    /** 仅对「常规数据行」响应侧滑；头/脚/空页等返回 0 位移标志。 */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        //是否响应多拽事件或者侧滑事件
        return if (viewHolder.isXRoutineLayout()) makeMovementFlags(0, 0)
        else makeMovementFlags(0, flags.invoke(recyclerView, viewHolder))
    }

    /** 仅允许同 [itemViewType] 之间判定移动（此处用于占位，侧滑删除不依赖交换）。 */
    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return source.itemViewType == target.itemViewType
    }

    /**
     * 侧滑结束：若 [swipe] 未消费事件，则默认从 [NodeAdapter] 中移除当前节点（含子树由 Adapter 处理）。
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder !is XHolder<*>) return
        if (swipe?.invoke(viewHolder, direction) != true) {
            val adapter = viewHolder.xAdapter as? NodeAdapter<*, *> ?: return
            adapter.removeNodePosition(viewHolder.getXPosition())
        }
    }

    /** 视为「已滑动足够距离」的阈值，传给 [ItemTouchHelper]。 */
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return threshold
    }

    /** 侧滑过程中裁剪绘制区域，避免内容溢出。 */
    override fun onChildDrawOver(
        canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE
            && !viewHolder.isXRoutineLayout()
        ) {
            val itemView = viewHolder.itemView
            canvas.save()
            if (dX > 0) {
                canvas.clipRect(
                    itemView.left.toFloat(), itemView.top.toFloat(),
                    itemView.left + dX, itemView.bottom.toFloat()
                )
                canvas.translate(itemView.left.toFloat(), itemView.top.toFloat())
            } else {
                canvas.clipRect(
                    itemView.right + dX, itemView.top.toFloat(),
                    itemView.right.toFloat(), itemView.bottom.toFloat()
                )
                canvas.translate(itemView.right + dX, itemView.top.toFloat())
            }
            canvas.restore()
        }
    }

    /** 开始侧滑时回调 [start]。 */
    override fun onStart(viewHolder: RecyclerView.ViewHolder?) {
        start?.invoke(viewHolder)
    }

    /** 手指离开视图、动画清理阶段回调 [end]。 */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        end?.invoke(recyclerView, viewHolder)
    }


    private fun RecyclerView.ViewHolder.isXRoutineLayout(): Boolean {
        return (this as? XHolder<*>)?.isRoutineLayout() ?: false
    }

    companion object {
        private const val TAG = "SwipeDelete"
    }
}
