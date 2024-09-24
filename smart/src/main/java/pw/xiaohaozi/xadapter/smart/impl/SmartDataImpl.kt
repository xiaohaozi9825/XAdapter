package pw.xiaohaozi.xadapter.smart.impl

import androidx.annotation.IntRange
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.proxy.ObservableList
import pw.xiaohaozi.xadapter.smart.proxy.SmartDataProxy
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer
import pw.xiaohaozi.xadapter.smart.proxy.XProxy
import pw.xiaohaozi.xadapter.smart.ext.remove
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
    fun getAdapter(): XAdapter<*, *> {
        return when (val e = employer) {
            is XEmployer -> e.getEmployerAdapter()
            else -> throw NullPointerException("找不到对应的Adapter对象")
        }
    }

    fun getDatas(): MutableList<D> = getAdapter().datas as MutableList<D>


    /**
     * 追加数据，可用于上拉加载更多
     *
     * @param list
     */
    override fun <L : Collection<D>> add(list: L) {
        if (list.isEmpty()) return
        getDatas().addAll(list)
        getAdapter().notifyItemRangeInserted(getDatas().size - list.size, list.size)
        notifyItemRangeInserted(getDatas().size - list.size, list.size)
    }

    /**
     * 增加一个数据
     *
     * @param data
     */
    override fun add(data: D) {
        getDatas().add(data)
        getAdapter().notifyItemInserted(getDatas().size - 1)
        notifyItemRangeInserted(getDatas().size - 1, 1)
    }

    /**
     * 增加一条数据到指定位置
     *
     * @param index
     * @param data
     */
    override fun add(@IntRange(from = 0) index: Int, data: D) {
        getDatas().add(index, data)
        getAdapter().notifyItemInserted(index)
        notifyItemRangeInserted(index, 1)
    }

    /**
     * 增加一条数据到指定位置
     *
     * @param index
     * @param data
     */
    override fun <L : Collection<D>> add(@IntRange(from = 0) index: Int, list: L) {
        getDatas().addAll(index, list)
        getAdapter().notifyItemRangeInserted(index, list.size)
        notifyItemRangeInserted(index, list.size)
    }

    /**
     * 移除指定位置数据
     *
     * @param index
     */
    override fun removeAt(@IntRange(from = 0) index: Int) {
        val data = getDatas()[index]
//        val count = getDatas().count { it == data }
//        if (count == 1) mAdapterSelectedImpl?.selectedCache?.remove(data)
        getDatas().removeAt(index)
        getAdapter().notifyItemRemoved(index)
        notifyItemRangeRemoved(index, 1)
    }

    /**
     * 从指定位置移除指定个数数据
     *
     * @param start 从第几个位置开始
     * @param count 移除多少个元素
     */
    override fun remove(@IntRange(from = 0) start: Int, @IntRange(from = 1) count: Int) {
//        for (index in start until start + count) {
//            val data = datas[index]
//            val count1 = getDatas().count { it == data }
//            if (count1 == 1) mAdapterSelectedImpl?.selectedCache?.remove(data)
//        }
        getDatas().remove(start, count)
        getAdapter().notifyItemRangeRemoved(start, count)
        notifyItemRangeRemoved(start, count)
    }

    /**
     * 移除指定数据
     *
     * @param data
     */
    override fun remove(data: D) {
//        val count = getDatas().count { it == data }
//        if (count == 1) mAdapterSelectedImpl?.selectedCache?.remove(data)
        val indexOf: Int = getDatas().indexOf(data)
        if (indexOf >= 0) {
            getDatas().remove(data)
            getAdapter().notifyItemRemoved(indexOf)
            notifyItemRangeRemoved(indexOf, 1)
        }
    }

    /**
     * 移除部分数据
     * @param list 这些数据必须是 datas 中存在的
     */
    override fun <L : Collection<D>> remove(list: L) {
        if (list.isEmpty()) return
//        list.forEach { data ->
//            val count = getDatas().count { it == data }
//            if (count == 1) mAdapterSelectedImpl?.selectedCache?.remove(data)
//        }
        getDatas().removeAll(list)
        getAdapter().notifyDataSetChanged()//list 在 datas 中的位置可能是不连续的，所以需要刷新全部数据
        notifyItemRangeRemoved(-1, list.size)//此处无法判断起始点
    }

    /**
     * 移除所有数据
     */
    override fun remove() {
        if (getDatas().isEmpty()) return
        getDatas().clear()
//        mAdapterSelectedImpl?.selectedCache?.clear()
        getAdapter().notifyDataSetChanged()
        notifyItemRangeRemoved(0, getDatas().size)
    }

    /**
     * 重置数据，可用于第一次加载数据或下拉刷新
     *
     * @param list 如果list是MutableList类型，则data==list；否则data！=list
     */
    override fun <L : MutableList<D>> refresh(list: L) {
//        mAdapterSelectedImpl?.selectedCache?.clear()
//        getAdapter().datas = list
//        getAdapter().notifyDataSetChanged()
//        notifyChanged()
    }

    override fun <L : Collection<D>> reset(list: L) {
//        mAdapterSelectedImpl?.selectedCache?.clear()
        getDatas().clear()
        getDatas().addAll(list)
        notifyChanged()
    }

    /**
     * 更新指定位置的数据
     *
     * @param index
     * @param data
     */
    override fun upDate(@IntRange(from = 0) index: Int, data: D) {
        getDatas()[index] = data
        getAdapter().notifyItemChanged(index)
        notifyItemRangeChanged(index, 1)
    }

    /**
     * 更新一个数据
     *
     * @param index 数据所在索引
     */
    override fun upDateAt(@IntRange(from = 0) index: Int) {
        getAdapter().notifyItemChanged(index)
        notifyItemRangeChanged(index, 1)
    }

    /**
     * 更新一个数据
     *
     * @param data 需要更新的数据，该数据必须是 datas 中存在的
     */
    override fun upDate(data: D) {
        val indexOf = getDatas().indexOf(data)
        if (indexOf > -1) {
            getAdapter().notifyItemChanged(indexOf)
            notifyItemRangeChanged(indexOf, 1)
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
            val indexOf = getDatas().indexOf(it)
            if (indexOf > -1) {
                getAdapter().notifyItemChanged(indexOf)
                notifyItemRangeChanged(indexOf, 1)
            }
        }
    }

    override fun swap(fromPosition: Int, toPosition: Int) {
        Collections.swap(getDatas(), fromPosition, toPosition)
        getAdapter().notifyItemMoved(fromPosition, toPosition)
        notifyItemRangeMoved(fromPosition, toPosition, toPosition - fromPosition)
    }


    private val callbacks: LinkedList<ObservableList.OnListChangedCallback<MutableList<D>>?> = LinkedList()

    override fun addOnListChangedCallback(callback: ObservableList.OnListChangedCallback<MutableList<D>>) {
        callbacks.add(callback)
    }

    override fun removeOnListChangedCallback(callback: ObservableList.OnListChangedCallback<MutableList<D>>) {
        callbacks.remove(callback)
    }

    private fun notifyChanged() {
        callbacks.forEach {
            it?.onChanged(getDatas())
        }
    }

    private fun notifyItemRangeChanged(positionStart: Int, itemCount: Int) {
        callbacks.forEach {
            it?.onItemRangeChanged(getDatas(), positionStart, itemCount)
        }
    }

    private fun notifyItemRangeInserted(positionStart: Int, itemCount: Int) {
        callbacks.forEach {
            it?.onItemRangeInserted(getDatas(), positionStart, itemCount)
        }
    }

    private fun notifyItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        callbacks.forEach {
            it?.onItemRangeMoved(getDatas(), fromPosition, toPosition, itemCount)
        }
    }

    private fun notifyItemRangeRemoved(positionStart: Int, itemCount: Int) {
        callbacks.forEach {
            it?.onItemRangeRemoved(getDatas(), positionStart, itemCount)
        }
    }

}