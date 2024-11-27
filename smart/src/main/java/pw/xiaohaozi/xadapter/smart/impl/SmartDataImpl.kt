package pw.xiaohaozi.xadapter.smart.impl

import androidx.annotation.IntRange
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.ext.removeRange
import pw.xiaohaozi.xadapter.smart.proxy.ObservableList
import pw.xiaohaozi.xadapter.smart.proxy.SmartDataProxy
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer
import pw.xiaohaozi.xadapter.smart.proxy.XProxy
import java.util.*

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/6 8:47
 */
class SmartDataImpl<Employer : XProxy<Employer>, VB : ViewBinding, D> : SmartDataProxy<Employer, VB, D> {
    override lateinit var employer: Employer
    private val adapter: XAdapter<*, D> by lazy {
        when (val e = employer) {
            is XEmployer -> e.getEmployerAdapter() as XAdapter<*, D>
            else -> throw XAdapterException("找不到对应的Adapter对象")
        }
    }

    private fun getData(): MutableList<D> = adapter.getData()
    override val callbacks: LinkedList<ObservableList.OnListChangedCallback<MutableList<D>>?> = LinkedList()


    /**
     * 追加数据，可用于上拉加载更多
     *
     * @param list
     */
    override fun <L : Collection<D>> add(list: L) {
        if (list.isEmpty()) return
        getData().addAll(list)
        val startPosition = adapter.itemCount - list.size - adapter.getFooterProviderCount()
        adapter.notifyItemRangeInserted(startPosition, list.size)
        notifyItemRangeInserted(startPosition, list.size)
    }

    /**
     * 增加一个数据
     *
     * @param data
     */
    override fun add(data: D) {
        getData().add(data)
        val startPosition = adapter.itemCount - 1 - adapter.getFooterProviderCount()
        adapter.notifyItemInserted(startPosition)
        notifyItemRangeInserted(startPosition, 1)
    }

    /**
     * 增加一条数据到指定位置
     *
     * @param index 相对于数据
     * @param data
     */
    override fun add(index: Int, data: D) {
        getData().add(index, data)
        val adapterPosition = adapter.getAdapterPosition(index)
        adapter.notifyItemInserted(adapterPosition)
        notifyItemRangeInserted(adapterPosition, 1)
    }

    /**
     * 增加一条数据到指定位置
     *
     * @param index 相对于datas的索引
     * @param data
     */
    override fun <L : Collection<D>> add(index: Int, list: L) {
        getData().addAll(index, list)
        val adapterPosition = adapter.getAdapterPosition(index)
        adapter.notifyItemRangeInserted(adapterPosition, list.size)
        notifyItemRangeInserted(adapterPosition, list.size)
    }

    /**
     * 移除指定位置数据
     *
     * @param index 相对于datas的索引
     */
    override fun removeAt(index: Int) {
        val data = getData().removeAt(index)
        val adapterPosition = adapter.getAdapterPosition(index)
        adapter.notifyItemRemoved(adapterPosition)
        notifyItemRangeRemoved(mutableListOf(data), adapterPosition, 1)
    }

    /**
     * 从指定位置移除指定个数数据
     *
     * @param start 从第几个位置开始，相对于数据
     * @param count 移除多少个元素
     */
    override fun remove(start: Int, count: Int) {
        val list = getData().removeRange(start, count)
        val adapterPosition = adapter.getAdapterPosition(start)
        adapter.notifyItemRangeRemoved(adapterPosition, count)
        notifyItemRangeRemoved(list, adapterPosition, count)
    }

    /**
     * 移除指定数据
     *
     * @param data
     */
    override fun remove(data: D) {
        val indexOf: Int = getData().indexOf(data)
        if (indexOf >= 0) {
            getData().remove(data)
            val adapterPosition = adapter.getAdapterPosition(indexOf)
            adapter.notifyItemRemoved(adapterPosition)
            notifyItemRangeRemoved(mutableListOf(data), adapterPosition, 1)
        }
    }

    /**
     * 移除部分数据
     * @param list 这些数据必须是 datas 中存在的
     */
    override fun <L : Collection<D>> remove(list: L) {
        if (list.isEmpty()) return
        getData().removeAll(list)
        adapter.notifyDataSetChanged()//list 在 datas 中的位置可能是不连续的，所以需要刷新全部数据
        notifyItemRangeRemoved(list.toMutableList(), -1, list.size)//此处无法判断起始点
    }

    /**
     * 移除所有数据
     */
    override fun remove() {
        val datas = getData()
        if (datas.isEmpty()) return
        val temp: MutableList<D> = ArrayList(datas)
        datas.clear()
        val startPosition = adapter.getAdapterPosition(0)
        adapter.notifyItemRangeRemoved(startPosition, temp.size)
        notifyItemRangeRemoved(temp, startPosition, temp.size)
    }

    /**
     * 重置数据，可用于第一次加载数据或下拉刷新
     *
     * @param list 如果list是MutableList类型，则data==list；否则data！=list
     */
    override fun <L : MutableList<D>> refresh(list: L) {
        adapter.setData(list)
        adapter.notifyDataSetChanged()
        notifyChanged()
    }

    override fun <L : Collection<D>> reset(list: L) {
        getData().clear()
        getData().addAll(list)
        adapter.notifyDataSetChanged()
        notifyChanged()
    }

    /**
     * 更新指定位置的数据
     *
     * @param index
     * @param data
     */
    override fun upDate(@IntRange(from = 0) index: Int, data: D) {
        getData()[index] = data
        val adapterPosition = adapter.getAdapterPosition(index)
        adapter.notifyItemChanged(adapterPosition)
        notifyItemRangeChanged(adapterPosition, 1)
    }

    /**
     * 更新一个数据
     *
     * @param index 数据所在索引
     */
    override fun upDateAt(@IntRange(from = 0) index: Int) {
        val adapterPosition = adapter.getAdapterPosition(index)
        adapter.notifyItemChanged(adapterPosition)
        notifyItemRangeChanged(adapterPosition, 1)
    }

    /**
     * 更新一个数据
     *
     * @param data 需要更新的数据，该数据必须是 datas 中存在的
     */
    override fun upDate(data: D) {
        val indexOf = getData().indexOf(data)
        if (indexOf > -1) {
            val adapterPosition = adapter.getAdapterPosition(indexOf)
            adapter.notifyItemChanged(adapterPosition)
            notifyItemRangeChanged(adapterPosition, 1)
        }
    }

    /**
     * 批量更新数据
     *
     * @param list 需要更新的数据，这些数据必须是 datas 中存在的
     */
    override fun <L : Collection<D>> upDate(list: L) {
        if (list.isEmpty()) return
        list.forEach {
            val indexOf = getData().indexOf(it)
            if (indexOf > -1) {
                val adapterPosition = adapter.getAdapterPosition(indexOf)
                adapter.notifyItemChanged(adapterPosition)
                notifyItemRangeChanged(adapterPosition, 1)
            }
        }
    }

    override fun swap(fromPosition: Int, toPosition: Int) {
        Collections.swap(getData(), fromPosition, toPosition)
        val fromAdapterPosition = adapter.getAdapterPosition(fromPosition)
        val toAdapterPosition = adapter.getAdapterPosition(toPosition)

        adapter.notifyItemMoved(fromAdapterPosition, toAdapterPosition)
        notifyItemRangeMoved(fromAdapterPosition, toAdapterPosition, toAdapterPosition - fromAdapterPosition)
    }


    override fun submitList(list: List<D>, commitCallback: Runnable) {
        adapter.differ.submitList(list, commitCallback)
    }

    override fun submitList(list: List<D>) {
        adapter.differ.submitList(list)
    }


    override fun setDiffer(
        diffCallback: DiffUtil.ItemCallback<D>,
        listener: AsyncListDiffer.ListListener<D>?
    ): Employer {
        adapter.differ = AsyncListDiffer<D>(
            XAdapterListUpdateCallback(adapter, callbacks),
            AsyncDifferConfig.Builder(diffCallback).build()
        )
        listener?.let { adapter.differ.addListListener(it) }
        return employer
    }

    override fun setDiffer(
        config: AsyncDifferConfig<D>,
        listener: AsyncListDiffer.ListListener<D>?
    ): Employer {
        adapter.differ = AsyncListDiffer(XAdapterListUpdateCallback(adapter, callbacks), config)
        listener?.let { adapter.differ.addListListener(it) }
        return employer
    }

    override fun addOnListChangedCallback(callback: ObservableList.OnListChangedCallback<MutableList<D>>) {
        callbacks.add(callback)
    }

    override fun removeOnListChangedCallback(callback: ObservableList.OnListChangedCallback<MutableList<D>>) {
        callbacks.remove(callback)
    }

    private fun notifyChanged() {
        callbacks.forEach {
            it?.onChanged(getData())
        }
    }

    private fun notifyItemRangeChanged(positionStart: Int, itemCount: Int) {
        callbacks.forEach {
            it?.onItemRangeChanged(getData(), positionStart, itemCount)
        }
    }

    private fun notifyItemRangeInserted(positionStart: Int, itemCount: Int) {
        callbacks.forEach {
            it?.onItemRangeInserted(getData(), positionStart, itemCount)
        }
    }

    private fun notifyItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        callbacks.forEach {
            it?.onItemRangeMoved(getData(), fromPosition, toPosition, itemCount)
        }
    }

    private fun notifyItemRangeRemoved(changeDatas: MutableList<D>, positionStart: Int, itemCount: Int) {
        callbacks.forEach {
            it?.onItemRangeRemoved(getData(), changeDatas, positionStart, itemCount)
        }
    }


    class XAdapterListUpdateCallback<D>(
        private val adapter: XAdapter<*, D>,
        private val callbacks: LinkedList<ObservableList.OnListChangedCallback<MutableList<D>>?>
    ) : ListUpdateCallback {

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeInserted(adapter.getAdapterPosition(position), count)
            callbacks.forEach {
                it?.onItemRangeInserted(adapter.getData(), adapter.getAdapterPosition(position), count)
            }
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyItemRangeRemoved(adapter.getAdapterPosition(position), count)
            callbacks.forEach {
                it?.onItemRangeRemoved(adapter.getData(), null, adapter.getAdapterPosition(position), count)
            }
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyItemMoved(adapter.getAdapterPosition(fromPosition), adapter.getAdapterPosition(toPosition))
            callbacks.forEach {
                it?.onItemRangeMoved(adapter.getData(), adapter.getAdapterPosition(fromPosition), adapter.getAdapterPosition(toPosition), 1)
            }
        }


        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(adapter.getAdapterPosition(position), count, payload)
            callbacks.forEach {
                it?.onItemRangeChanged(adapter.getData(), adapter.getAdapterPosition(position), count)
            }
        }
    }

}