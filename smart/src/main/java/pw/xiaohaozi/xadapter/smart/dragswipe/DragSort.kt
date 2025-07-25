package pw.xiaohaozi.xadapter.smart.dragswipe

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.holder.isXRoutineLayout
import java.util.*
import kotlin.collections.ArrayList

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
class DragSort(
    private val threshold: Float = 0.9f,
    private val flags: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int = { _, _ -> UP or DOWN or START or END },
    private val start: ((viewHolder: RecyclerView.ViewHolder?) -> Unit)? = null,
    private val end: ((recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Unit)? = null,
    private val onMove: ((
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) -> Boolean)? = null,
    private val swap: ((
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
        fromPosition: Int,
        toPosition: Int,
    ) -> Unit)? = null,

    ) : DragCallback {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder.isXRoutineLayout()) makeMovementFlags(0, 0)
        else makeMovementFlags(flags.invoke(recyclerView, viewHolder), 0)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (source !is XHolder<*>) return false
        if (target !is XHolder<*>) return false

        if (onMove != null) return onMove.invoke(recyclerView, source, target)
        val adapter = recyclerView.adapter as? SmartAdapter<*, Any> ?: return false
        if (source.isXRoutineLayout()) return false
        if (target.isXRoutineLayout()) return false
        recyclerView.parent.requestDisallowInterceptTouchEvent(true)
        //得到当拖拽的viewHolder的Position
        val fromPosition: Int = adapter.getDataPosition(source.getXPosition())
        //拿到当前拖拽到的item的viewHolder
        val toPosition = adapter.getDataPosition(target.getXPosition())
        Log.i("交换数据", "onMove: fromPosition = $fromPosition  ==  toPosition = $toPosition")
        swap?.invoke(recyclerView, source, target, fromPosition, toPosition)
        if (adapter.isDifferMode()) {
            val temp = ArrayList(adapter.getDataList())
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(temp, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(temp, i, i - 1)
                }
            }
            adapter.submitList(temp)
        } else {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    adapter.swap(i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    adapter.swap(i, i - 1)
                }
            }
        }
        return true
    }

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return threshold
    }

    override fun onStart(viewHolder: RecyclerView.ViewHolder?) {
        start?.invoke(viewHolder)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        end?.invoke(recyclerView, viewHolder)
    }


}
