package pw.xiaohaozi.xadapter.smart.dragswipe

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.dragswipe.ItemDrag
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider
import java.util.*

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
    private val flags: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int = { _, _ -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END },
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
        return if (isViewCreateByAdapter(viewHolder)) makeMovementFlags(0, 0)
        else makeMovementFlags(flags.invoke(recyclerView, viewHolder), 0)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (onMove != null) return onMove.invoke(recyclerView, source, target)

        if (isViewCreateByAdapter(source)) return false
        if (isViewCreateByAdapter(target)) return true
        val adapter = recyclerView.adapter as SmartAdapter<*, *>
        recyclerView.parent.requestDisallowInterceptTouchEvent(true)
        //得到当拖拽的viewHolder的Position
        val fromPosition: Int = adapter.getCustomPosition(source.bindingAdapterPosition)
        //拿到当前拖拽到的item的viewHolder
        val toPosition = adapter.getCustomPosition(target.bindingAdapterPosition)
        Log.i("交换数据", "onMove: fromPosition = $fromPosition  ==  toPosition = $toPosition")
        swap?.invoke(recyclerView, source, target, fromPosition, toPosition)
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(adapter.datas, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(adapter.datas, i, i - 1)
            }
        }
        adapter.notifyItemMoved(
            adapter.getHeaderProviderCount() + fromPosition,
            adapter.getHeaderProviderCount() + toPosition
        )

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

    //是否有用户自己创建的ViewHolder，非特殊ViewHolder，如头布局，空布局，错误布局，底部布局
    private fun isViewCreateByAdapter(viewHolder: RecyclerView.ViewHolder?): Boolean {
        viewHolder?.let {
            val adapterProxy = viewHolder.bindingAdapter as? SmartAdapter<*, *>
            return adapterProxy?.getCustomPosition(viewHolder.bindingAdapterPosition) == -1
        }
        return false
    }

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
    flags: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int = { _, _ -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END },
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

///**
// * 拖拽排序
// *
// * @param threshold 设置用户在拖拽视图时应该移动视图的比例。在视图移动到这个位置之后，ItemTouchHelper开始检查视图下方是否有可能的删除。一个浮点值，表示视图大小的百分比。缺省值为。1f。
// * @param flags 触发方向
// * @param start 开始拖拽
// * @param end 结束拖拽（松开手就会调用）
// * @param onMove 被拖拽的item多拽到其他item位置上是调用,该参数会替换掉现有的onMove逻辑
// * @param swap 当两个item交换时调用
// */
//fun <VB : ViewBinding, D : Any> SmartProvider<SmartAdapter<ViewBinding, Any>, VB, D?>.dragSort(
//    threshold: Float = 0.1f,
//    start: ((viewHolder: RecyclerView.ViewHolder?) -> Unit)? = null,
//    end: ((recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Unit)? = null,
//    onMove: ((
//        recyclerView: RecyclerView,
//        source: RecyclerView.ViewHolder,
//        target: RecyclerView.ViewHolder
//    ) -> Boolean)? = null,
//    swap: ((
//        recyclerView: RecyclerView,
//        source: RecyclerView.ViewHolder,
//        target: RecyclerView.ViewHolder
//    ) -> Unit)? = null,
//): SmartProvider<SmartAdapter<ViewBinding, Any>, VB, D?> {
//    adapter.onRecyclerViewChange {
//        it ?: return@onRecyclerViewChange
//        ItemTouchHelper(
//            ItemDrag(
//                DragSort(
//                    threshold, { _, holder ->
//                        Log.i("ceshiceshi", "dragSort: ${holder.itemViewType}--${adapterProxy?.getItemViewType(this)}")
//                        if (holder.itemViewType == adapterProxy?.getItemViewType(this)) ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
//                        else 0
//                    }, start,
//                    end, onMove, swap
//                )
//            )
//        ).attachToRecyclerView(it)
//    }
//
//    return this
//}
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
    flags: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int = { _, holder -> if (holder.itemViewType == getItemViewType()) UP or DOWN or START or END else 0 },
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