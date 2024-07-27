package pw.xiaohaozi.xadapter.smart.adapter

import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pw.xiaohaozi.xadapter.smart.impl.ListenerImpl
import pw.xiaohaozi.xadapter.smart.impl.SmartDataImpl
import pw.xiaohaozi.xadapter.smart.proxy.ListenerProxy
import pw.xiaohaozi.xadapter.smart.proxy.SmartDataProxy
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer
import kotlin.coroutines.CoroutineContext

/**
 * Adapter集
 * 描述：包含了SmartAdapter基础功能，数据操作功能，选择功能，事件监听功能
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 9:10
 */
open class XAdapter<VB : ViewBinding, D>(
    private val dataImpl: SmartDataImpl<XAdapter<VB, D>, VB, D> = SmartDataImpl(), private val listenerImpl: ListenerImpl<XAdapter<VB, D>, VB, D> = ListenerImpl()
) : SmartAdapter<VB, D>(), CoroutineScope, XEmployer, SmartDataProxy<XAdapter<VB, D>, VB, D> by dataImpl, ListenerProxy<XAdapter<VB, D>, VB, D> by listenerImpl {
    init {
        init()
    }

    private fun init() {
        init(this)
    }

    override var employer: XAdapter<VB, D>
        get() = this
        set(value) {}

    override fun init(employer: XAdapter<VB, D>) {
        dataImpl.init(employer)
        listenerImpl.init(employer)
    }

    override fun getEmployerAdapter(): SmartAdapter<VB, D> {
        return this
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Main

}