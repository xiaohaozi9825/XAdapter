package pw.xiaohaozi.xadapter.smart.dragswipe

import androidx.recyclerview.widget.RecyclerView

interface DragCallback {

    fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int

    fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean

    fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float
    fun onStart(viewHolder: RecyclerView.ViewHolder?)
    fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder)
}
