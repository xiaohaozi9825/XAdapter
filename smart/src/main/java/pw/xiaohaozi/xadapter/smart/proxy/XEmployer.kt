package pw.xiaohaozi.xadapter.smart.proxy

import pw.xiaohaozi.xadapter.smart.adapter.XAdapter

/**
 * 宿主类接口
 * 描述：宿主，用于提供Adapter，Adapter和Provider都需要实现该接口
 *
 * 作者：小耗子
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 9:26
 */
interface XEmployer {
    /**
     * 返回承载列表与数据的 [XAdapter]；[SmartDataProxy]、[EventProxy]、[SelectedProxy] 等通过该方法统一访问 Adapter。
     */
    fun getEmployerAdapter(): XAdapter<*, *, *>
}
