package pw.xiaohaozi.xadapter.smart.adapter

import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pw.xiaohaozi.xadapter.smart.entity.EMPTY
import pw.xiaohaozi.xadapter.smart.entity.ERROR
import pw.xiaohaozi.xadapter.smart.entity.FOOTER
import pw.xiaohaozi.xadapter.smart.entity.HEADER
import pw.xiaohaozi.xadapter.smart.ext.OnBindParams
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.impl.AdapterSelectedImpl
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.impl.SmartDataImpl
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy
import pw.xiaohaozi.xadapter.smart.proxy.SelectedProxy
import pw.xiaohaozi.xadapter.smart.proxy.SmartDataProxy
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer
import kotlin.coroutines.CoroutineContext

/**
 * Adapter集
 * 描述：包含了XAdapter基础功能，数据操作功能，选择功能，事件监听功能
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 9:10
 */
open class SmartAdapter<VB : ViewBinding, D>(
    private val dataImpl: SmartDataImpl<SmartAdapter<VB, D>, VB, D> = SmartDataImpl(), //
    private val eventImpl: EventImpl<SmartAdapter<VB, D>, VB, D> = EventImpl(),//
    private val selectedImpl: AdapterSelectedImpl<SmartAdapter<VB, D>, VB, D> = AdapterSelectedImpl()//
) : XAdapter<VB, D>(),//继承Adapter
    CoroutineScope, //协成
    XEmployer, //宿主
    SmartDataProxy<SmartAdapter<VB, D>, VB, D> by dataImpl,//数据
    EventProxy<SmartAdapter<VB, D>, VB, D> by eventImpl,//
    SelectedProxy<SmartAdapter<VB, D>, VB, D> by selectedImpl //
{
    init {
        initProxy()
    }

    private fun initProxy() {
        initProxy(this)
    }

    override var employer: SmartAdapter<VB, D>
        get() = this
        set(value) {}

    final override fun initProxy(employer: SmartAdapter<VB, D>) {
        dataImpl.initProxy(employer)
        eventImpl.initProxy(employer)
        selectedImpl.initProxy(employer)
    }

    override fun getEmployerAdapter(): XAdapter<VB, D> {
        return this
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Main + CoroutineName("XAdapterCoroutine")

    override operator fun plus(provider: TypeProvider<*, *>): SmartAdapter<VB, D> {
        addProvider(provider)
        return this
    }


    inline fun <reified vb : ViewBinding> addHeader(
        tag: String = "",
        noinline bind: (SmartAdapter<VB, D>.(holder: XHolder<vb>) -> Unit)? = null
    ): SmartAdapter<VB, D> {
        val provider = object : SmartProvider<vb, HEADER>(this) {

            override fun onCreated(holder: XHolder<vb>) {
            }

            override fun onBind(holder: XHolder<vb>, data: HEADER, position: Int) {
                bind?.invoke(this@SmartAdapter, holder)
            }

            override fun isFixedViewType(): Boolean {
                return true
            }
        }
        provider.setOnClickListener { holder, data, position, view ->
            Toast.makeText(view.context, "点击头${data.tag}", Toast.LENGTH_SHORT).show()
        }
        addHeaderProvider(provider, HEADER(tag))
        return this
    }

    fun removeHeader(tag: String = ""): SmartAdapter<VB, D> {
        removeHeaderProvider(tag)
        return this
    }

    inline fun <reified vb : ViewBinding> addFooter(
        tag: String = "",
        noinline bind: (SmartAdapter<VB, D>.(holder: XHolder<vb>) -> Unit)? = null
    ): SmartAdapter<VB, D> {
        val provider = object : SmartProvider<vb, FOOTER>(this) {

            override fun onCreated(holder: XHolder<vb>) {
            }

            override fun onBind(holder: XHolder<vb>, data: FOOTER, position: Int) {
                bind?.invoke(this@SmartAdapter, holder)
            }

            override fun isFixedViewType(): Boolean {
                return true
            }
        }
        provider.setOnClickListener { holder, data, position, view ->
            Toast.makeText(view.context, "点击脚${data.tag}", Toast.LENGTH_SHORT).show()
        }
        addFooterProvider(provider, FOOTER(tag))
        return this
    }

    fun removeFooter(tag: String = ""): SmartAdapter<VB, D> {
        removeFooterProvider(tag)
        return this
    }


    inline fun <reified vb : ViewBinding> setEmpty(noinline bind: ((holder: XHolder<vb>) -> Unit)? = null): SmartAdapter<VB, D> {
        val provider = object : SmartProvider<vb, EMPTY?>(this) {

            override fun isFixedViewType(): Boolean {
                return true
            }

            override fun onBind(holder: XHolder<vb>, data: EMPTY?, position: Int) {
                bind?.invoke(holder)
            }

            override fun onCreated(holder: XHolder<vb>) {
            }

        }
        setEmptyProvider(provider, EMPTY)
        return this
    }

    fun deleteEmpty(): SmartAdapter<VB, D> {
        deleteEmptyProvider()
        return this
    }

    inline fun <reified vb : ViewBinding> setError(noinline bind: ((holder: XHolder<vb>) -> Unit)? = null): SmartAdapter<VB, D> {
        val provider =
            object : SmartProvider<vb, ERROR?>(this) {

                override fun isFixedViewType(): Boolean {
                    return true
                }

                override fun onBind(holder: XHolder<vb>, data: ERROR?, position: Int) {
                    bind?.invoke(holder)
                }

                override fun onCreated(holder: XHolder<vb>) {

                }

            }
        setErrorProvider(provider, ERROR)
        return this
    }

    fun deleteError(): SmartAdapter<VB, D> {
        deleteErrorProvider()
        return this
    }

}