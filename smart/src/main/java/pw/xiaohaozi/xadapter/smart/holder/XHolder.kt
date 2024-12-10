package pw.xiaohaozi.xadapter.smart.holder


import androidx.recyclerview.widget.RecyclerView
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

    override val coroutineContext: CoroutineContext
            by lazy { SupervisorJob(getXAdapter().coroutineContext.job) + CoroutineName("XHolderCoroutine") }

    fun getXAdapter(): XAdapter<*, *> {
        return bindingAdapter as XAdapter<*, *>
    }

    /**
     * 是否是常规布局
     * 非特殊ViewHolder，如头布局，空布局，错误布局，底部布局
     */

    fun isRoutineLayout(): Boolean {
        val adapterProxy = getXAdapter()
        val dataPosition = adapterProxy.getDataPosition(bindingAdapterPosition)
        return dataPosition < 0 || dataPosition >= adapterProxy.getData().size
    }
}

/**
 * 是否是常规布局
 * 该方法不对外开放，如需使用，请使用XHolder类中成员方法isRoutineLayout()
 */
internal fun RecyclerView.ViewHolder.isXRoutineLayout(): Boolean {
    return (this as? XHolder<*>)?.isRoutineLayout() ?: false
}

