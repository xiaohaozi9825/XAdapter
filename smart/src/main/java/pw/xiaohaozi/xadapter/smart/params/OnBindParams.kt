package pw.xiaohaozi.xadapter.smart.params

import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import pw.xiaohaozi.xadapter.smart.holder.XHolder

/**
 * 单次 bind 的绑定参数；同时实现 [kotlinx.coroutines.CoroutineScope]（委托给本次 bind 作用域），
 * 可在 `withType { it.launch { } }` 中与 Adapter 级 `this.launch` 区分使用。
 */
data class OnBindParams<VB : ViewBinding, D>(
    val holder: XHolder<VB>,
    val data: D,
    val position: Int,
    val payloads: List<Any?>,
    val scope: CoroutineScope,
) : CoroutineScope by scope {
    val binding: VB
        get() = holder.binding
}