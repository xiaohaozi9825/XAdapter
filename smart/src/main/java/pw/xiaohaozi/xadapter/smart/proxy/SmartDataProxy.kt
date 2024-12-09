package pw.xiaohaozi.xadapter.smart.proxy

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import java.util.LinkedList

/**
 * 数据代理接口
 * 描述：负责所有数据增删改操作
 *
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/6 8:45
 */
interface SmartDataProxy<Employer : XProxy<Employer>, VB : ViewBinding, D> :
    XProxy<Employer>, ObservableList<D> {
    val callbacks: LinkedList<ObservableList.OnListChangedCallback<MutableList<D>>?>

    /**
     * 设置数据
     * 会替换原来的数组对象
     * Differ模式下不可用
     */
    fun <L : MutableList<D>> setList(list: L)

    /**
     * 刷新数据
     * 会保留原数组对象
     * Differ模式下不可用
     */
    fun <L : Collection<D>> refresh(list: L)

    /**
     * 添加数据
     * Differ模式下不可用
     */
    fun <L : Collection<D>> add(list: L)

    /**
     * 添加数据
     * Differ模式下不可用
     */
    fun add(data: D)

    /**
     * 添加数据
     * Differ模式下不可用
     */
    fun add(index: Int, data: D)

    /**
     * 添加数据
     * Differ模式下不可用
     */
    fun <L : Collection<D>> add(index: Int, list: L)

    /**
     * 删除数据
     * Differ模式下不可用
     */
    fun removeAt(index: Int)

    /**
     * 删除数据
     * Differ模式下不可用
     */
    fun remove(start: Int, count: Int)

    /**
     * 删除数据
     * Differ模式下不可用
     */
    fun remove(data: D)

    /**
     * 删除数据
     * 改方法会刷新整个列表
     * Differ模式下不可用
     */
    fun <L : Collection<D>> remove(list: L)

    /**
     * 删除数据
     * Differ模式下不可用
     */
    fun remove()

    /**
     * 修改数据
     */
    fun update(index: Int, data: D, payload: Any? = null)

    /**
     * 修改数据
     */
    fun updateAt(index: Int, payload: Any? = null)

    /**
     * 修改数据
     */
    fun update(data: D, payload: Any? = null)

    /**
     * 修改数据
     */
    fun <L : Collection<D>> update(list: L, payload: Any? = null)

    /**
     * 交换数据
     * Differ模式下不可用
     */
    fun swap(fromPosition: Int, toPosition: Int)

    /**
     * 使用Differ算法迭代数据
     */
    fun setDiffer(
        diffCallback: DiffUtil.ItemCallback<D>,
        listener: AsyncListDiffer.ListListener<D> = AsyncListDiffer.ListListener<D> { _, _ -> }
    ): Employer

    /**
     * 使用Differ算法迭代数据
     */
    fun setDiffer(
        config: AsyncDifferConfig<D>,
        listener: AsyncListDiffer.ListListener<D> = AsyncListDiffer.ListListener<D> { _, _ -> }
    ): Employer

    /**
     * 在Differ模式下更新数据
     */
    fun submitList(list: List<D>)

    /**
     * 在Differ模式下更新数据
     */
    fun submitList(list: List<D>, commitCallback: Runnable)
}