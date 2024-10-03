package pw.xiaohaozi.xadapter.smart.dragswipe

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import pw.xiaohaozi.xadapter.smart.dragswipe.ItemSwipe

interface SwipeCallback {
    var itemSwipe: ItemSwipe
    fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int

    fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean

    //侧滑完成后会回调这里，需要在这里执行删除操作
    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)

    fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float

    fun onChildDrawOver(
        canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    )
    fun onStart(viewHolder: ViewHolder?)
    fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder)
}
