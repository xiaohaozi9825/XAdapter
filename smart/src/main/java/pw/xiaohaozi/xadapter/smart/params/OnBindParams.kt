package pw.xiaohaozi.xadapter.smart.params

import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import pw.xiaohaozi.xadapter.smart.holder.XHolder

/**
 * 单次 bind 的绑定参数；同时实现 [kotlinx.coroutines.CoroutineScope]（委托给本次 bind 作用域），
 * 可在 `withType { it.launch { } }` 中与 Adapter 级 `this.launch` 区分使用。
 *
 * @param holder 当前项 ViewHolder
 * @param data 当前项数据
 * @param position Adapter 中的位置（含头布局时需用 [pw.xiaohaozi.xadapter.smart.adapter.XAdapter.getDataPosition] 换算数据索引）
 * @param payloads 局部刷新 payload 列表
 * @param scope 与本次绑定生命周期对齐的协程作用域
 */
data class OnBindParams<VB : ViewBinding, D>(
    val holder: XHolder<VB>,
    val data: D,
    val position: Int,
    val payloads: List<Any?>,
    val scope: CoroutineScope,
) : CoroutineScope by scope {
    /** 当前项 ViewBinding，等价于 [holder.binding]。 */
    val binding: VB
        get() = holder.binding
}