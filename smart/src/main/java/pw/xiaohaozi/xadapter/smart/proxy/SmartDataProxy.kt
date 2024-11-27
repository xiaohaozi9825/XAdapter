package pw.xiaohaozi.xadapter.smart.proxy

import androidx.annotation.IntRange
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.impl.SmartDataImpl
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

    fun <L : MutableList<D>> refresh(list: L)
    fun <L : Collection<D>> reset(list: L)
    fun <L : Collection<D>> add(list: L)
    fun add(data: D)
    fun add(@IntRange(from = 0.toLong()) index: Int, data: D)
    fun <L : Collection<D>> add(@IntRange(from = 0.toLong()) index: Int, list: L)
    fun removeAt(@IntRange(from = 0.toLong()) index: Int)
    fun remove(@IntRange(from = 0.toLong()) start: Int, @IntRange(from = 1.toLong()) count: Int)
    fun remove(data: D)
    fun <L : Collection<D>> remove(list: L)
    fun remove()
    fun upDate(@IntRange(from = 0.toLong()) index: Int, data: D)
    fun upDateAt(@IntRange(from = 0.toLong()) index: Int)
    fun upDate(data: D)
    fun <L : Collection<D>> upDate(list: L)
    fun swap(fromPosition: Int, toPosition: Int)
    fun setDiffer(
        diffCallback: DiffUtil.ItemCallback<D>,
        listener: AsyncListDiffer.ListListener<D>? = AsyncListDiffer.ListListener<D> { _, _ -> }
    ): Employer

    fun setDiffer(
        config: AsyncDifferConfig<D>,
        listener: AsyncListDiffer.ListListener<D>? = AsyncListDiffer.ListListener<D> { _, _ -> }
    ): Employer

    fun submitList(list: List<D>)
    fun submitList(list: List<D>, commitCallback: Runnable)
}