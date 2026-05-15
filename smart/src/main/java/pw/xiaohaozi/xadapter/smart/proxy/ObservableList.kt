package pw.xiaohaozi.xadapter.smart.proxy

/**
 * 可观察的数据列表：在列表结构或内容变化时通知已注册的 [OnListChangedCallback]。
 *
 * 典型用途：与 [pw.xiaohaozi.xadapter.smart.impl.SmartDataImpl] 配合，将 RecyclerView 的 diff/notify 同步给选中等业务模块。
 */
interface ObservableList<T> {
    /** 注册列表变更监听。 */
    fun addOnListChangedCallback(callback: OnListChangedCallback<MutableList<T>>)

    /** 移除已注册的监听。 */
    fun removeOnListChangedCallback(callback: OnListChangedCallback<MutableList<T>>)

    /**
     * 列表变更回调抽象类；各方法对应一次数据区或 Adapter 层的批量变更语义。
     *
     * @param T 一般为 [MutableList] 类型的发送方引用。
     */
    abstract class OnListChangedCallback<T> {
        /** 整表替换或无法细化为范围事件时回调。 */
        abstract fun onChanged(sender: T, payload: Any?)

        /** 连续区间内的 item 内容变更。 */
        abstract fun onItemRangeChanged(sender: T, positionStart: Int, itemCount: Int, payload: Any?)

        /** 连续区间插入。 */
        abstract fun onItemRangeInserted(sender: T, positionStart: Int, itemCount: Int, payload: Any?)

        /** 连续区间移动（可能跨距）。 */
        abstract fun onItemRangeMoved(sender: T, fromPosition: Int, toPosition: Int, itemCount: Int, payload: Any?)

        /** 按起始位置与数量移除。 */
        abstract fun onItemRangeRemoved(sender: T, positionStart: Int, itemCount: Int, payload: Any?)

        /** 按被移除的数据集合通知（位置不连续时使用）。 */
        abstract fun onItemRangeRemoved(sender: T, changeDatas: T?, payload: Any?)
    }
}
