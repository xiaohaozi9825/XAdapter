package pw.xiaohaozi.xadapter.smart.proxy

interface ObservableList<T> {
    fun addOnListChangedCallback(callback: OnListChangedCallback<MutableList<T>>)
    fun removeOnListChangedCallback(callback: OnListChangedCallback<MutableList<T>>)
    abstract class OnListChangedCallback<T> {
        abstract fun onChanged(sender: T, payload: Any?)
        abstract fun onItemRangeChanged(sender: T, positionStart: Int, itemCount: Int, payload: Any?)
        abstract fun onItemRangeInserted(sender: T, positionStart: Int, itemCount: Int, payload: Any?)
        abstract fun onItemRangeMoved(sender: T, fromPosition: Int, toPosition: Int, itemCount: Int, payload: Any?)
        abstract fun onItemRangeRemoved(sender: T, positionStart: Int, itemCount: Int, payload: Any?)
        abstract fun onItemRangeRemoved(sender: T, changeDatas: T?, payload: Any?)
    }
}