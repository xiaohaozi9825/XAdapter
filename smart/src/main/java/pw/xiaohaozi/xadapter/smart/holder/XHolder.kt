package pw.xiaohaozi.xadapter.smart.holder


import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import kotlin.coroutines.CoroutineContext

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/8/10 20:09
 */
open class XHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root), CoroutineScope {
    val TAG = "XHolder"
    var xAdapter: XAdapter<*, *>? = null
        internal set
    var data: Any? = null
        internal set

    override val coroutineContext: CoroutineContext
            by lazy { SupervisorJob(xAdapter!!.coroutineContext.job) + CoroutineName("XHolderCoroutine") }


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
        else xAdapter?.getDataList()?.indexOf(data) ?: NO_POSITION
    }

    /**
     * 是否是常规布局
     * 非特殊ViewHolder，如头布局，空布局，错误布局，底部布局
     */

    fun isRoutineLayout(): Boolean {
        val adapterProxy = xAdapter ?: return false
        val dataPosition = adapterProxy.getDataPosition(adapterPosition)
        return dataPosition < 0 || dataPosition >= adapterProxy.getDataList().size
    }
}

/**
 * 是否是常规布局
 * 该方法不对外开放，如需使用，请使用XHolder类中成员方法isRoutineLayout()
 */
internal fun RecyclerView.ViewHolder.isXRoutineLayout(): Boolean {
    return (this as? XHolder<*>)?.isRoutineLayout() ?: false
}

