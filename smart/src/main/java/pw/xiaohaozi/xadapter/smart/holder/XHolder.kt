package pw.xiaohaozi.xadapter.smart.holder


import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.job
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter

/**
 *
 * 描述：
 * 作者：小耗子
 * 创建时间：2022/8/10 20:09
 */
open class XHolder<VB : ViewBinding>(
    val xAdapter: XAdapter<*, *, *>,
    val binding: VB,
) : RecyclerView.ViewHolder(binding.root) {

    private val holderJob: CompletableJob = SupervisorJob(xAdapter.coroutineContext.job)

    /** 每次 bind 重置；与 [bindCoroutineScope] 对应。 */
    private var bindJob: CompletableJob = SupervisorJob(holderJob)

    var data: Any? = null
        internal set

    /**
     * 在每次 [pw.xiaohaozi.xadapter.smart.adapter.XAdapter.bindViewHolder] 开头调用，取消上一轮绑定发起的协程。
     */
    internal fun resetBindScope() {
        bindJob.cancel()
        bindJob = SupervisorJob(holderJob)
    }

    /**
     * 当前一次 bind 的 [CoroutineScope]，供 [pw.xiaohaozi.xadapter.smart.provider.XProvider.onBind] 首参传入。
     */
    internal fun bindCoroutineScope(): CoroutineScope =
        CoroutineScope(bindJob + Dispatchers.Main + CoroutineName("XHolderBindCoroutine"))

    internal fun cancelHolderCoroutineChildren() {
        holderJob.cancelChildren()
    }

    /**
     * 获取ViewHolder对应的position
     * 由于adapterPosition 在列表item更新时会返回NO_POSITION，因此增加该方法，可以有效避免NO_POSITION出现；
     * 但是此方法并不代表永远不返回NO_POSITION，比如：
     * -ViewHolder被回收后，该方法将会返回NO_POSITION；
     * -当前item已被删除，但是删除动画还未结束，此时操作该该方法也会返回NO_POSITION
     */
    fun getXPosition(): Int {
        val position = adapterPosition
        return if (position != NO_POSITION) position
        else xAdapter.getDataList().indexOf(data).takeIf { it >= 0 } ?: NO_POSITION
    }

    /**
     * 是否是常规布局
     *
     * @return true-常规布局  false-特殊布局（如头布局、脚布局、空布局、缺省页）
     */
    fun isRoutineLayout(): Boolean {
        val dataPosition = xAdapter.getDataPosition(adapterPosition)
        return dataPosition < 0 || dataPosition >= xAdapter.getDataList().size
    }
}

/**
 * 是否是常规布局
 *
 * @return true-常规布局  false-特殊布局（如头布局、脚布局、空布局、缺省页）
 */
internal fun RecyclerView.ViewHolder.isXRoutineLayout(): Boolean {
    return (this as? XHolder<*>)?.isRoutineLayout() ?: false
}
