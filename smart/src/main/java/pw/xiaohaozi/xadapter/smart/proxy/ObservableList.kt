package pw.xiaohaozi.xadapter.smart.proxy

interface ObservableList<T> {
    fun addOnListChangedCallback(callback: OnListChangedCallback<MutableList<T>>)
    fun removeOnListChangedCallback(callback: OnListChangedCallback<MutableList<T>>)
    abstract class OnListChangedCallback<T> {
        abstract fun onChanged(sender: T)
        abstract fun onItemRangeChanged(sender: T, positionStart: Int, itemCount: Int)
        abstract fun onItemRangeInserted(sender: T, positionStart: Int, itemCount: Int)
        abstract fun onItemRangeMoved(sender: T, fromPosition: Int, toPosition: Int, itemCount: Int)
        abstract fun onItemRangeRemoved(sender: T, changeDatas: T?, positionStart: Int, itemCount: Int)
    }
}