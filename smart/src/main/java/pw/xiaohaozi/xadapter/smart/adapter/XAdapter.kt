package pw.xiaohaozi.xadapter.smart.adapter

import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pw.xiaohaozi.xadapter.smart.impl.AdapterSelectedImpl
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.impl.SmartDataImpl
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy
import pw.xiaohaozi.xadapter.smart.proxy.SelectedProxy
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
    private val dataImpl: SmartDataImpl<XAdapter<VB, D>, VB, D> = SmartDataImpl(), //
    private val eventImpl: EventImpl<XAdapter<VB, D>, VB, D> = EventImpl(),//
    private val selectedImpl: AdapterSelectedImpl<XAdapter<VB, D>, VB, D> = AdapterSelectedImpl()//
) : SmartAdapter<VB, D>(),//继承Adapter
    CoroutineScope, //协成
    XEmployer, //宿主
    SmartDataProxy<XAdapter<VB, D>, VB, D> by dataImpl,//数据
    EventProxy<XAdapter<VB, D>, VB, D> by eventImpl,//
    SelectedProxy<XAdapter<VB, D>, VB, D> by selectedImpl //
{
    init {
        initProxy()
    }

    private fun initProxy() {
        initProxy(this)
    }

    override var employer: XAdapter<VB, D>
        get() = this
        set(value) {}

    final override fun initProxy(employer: XAdapter<VB, D>) {
        dataImpl.initProxy(employer)
        eventImpl.initProxy(employer)
        selectedImpl.initProxy(employer)
    }

    override fun getEmployerAdapter(): SmartAdapter<VB, D> {
        return this
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Main + CoroutineName("XAdapterCoroutine")

    override operator fun plus(provider: TypeProvider<*, *>): XAdapter<VB, D> {
        addProvider(provider)
        return this
    }
}