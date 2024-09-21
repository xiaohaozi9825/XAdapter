package pw.xiaohaozi.xadapter.smart.impl

import android.os.Build
import android.view.View
import androidx.annotation.IntRange
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.smartadapter.utils.SelectedList
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.SmartHolder
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider
import pw.xiaohaozi.xadapter.smart.proxy.ObservableList
import pw.xiaohaozi.xadapter.smart.proxy.SelectedProxy
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer
import pw.xiaohaozi.xadapter.smart.proxy.XProxy

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2023年7月21日
 */
open class SmartProviderSelectedImpl<Employer : XProxy<Employer>, VB : ViewBinding, D> :
    SelectedProxy<Employer, VB, D> {
    override lateinit var employer: Employer
    private val adapter: SmartAdapter<*, *> by lazy {
        when (val e = employer) {
            is XEmployer -> e.getEmployerAdapter()
            else -> throw NullPointerException("找不到对应的Adapter对象")
        }
    }
    private val provider: SmartProvider<VB, D> by lazy {
        when (val e = employer) {
            is SmartProvider<*, *> -> e as SmartProvider<VB, D>
            else -> throw NullPointerException("找不到对应的Providerr对象")
        }
    }
    private val datas: MutableList<D> by lazy { adapter.datas as MutableList<D> }
    override fun initProxy(employer: Employer) {
        super.initProxy(employer)
        adapter.addOnViewHolderChanges(object : SmartAdapter.OnViewHolderChanges {
            override fun onCreated(provide: TypeProvider<*, *>, holder: SmartHolder<*>) {
                if (provide != employer) return
                initListener(holder)
            }

            override fun onBinding(holder: SmartHolder<*>, position: Int) {

            }

        })
    }

    private fun initListener(holder: SmartHolder<*>) {
        val selectedListener = this.selectedListener ?: return
        val viewId = selectedListener.first
        val tagger: View = viewId?.let { holder.itemView.findViewById(it) } ?: holder.itemView
        tagger.setOnClickListener {
            val position = holder.adapterPosition
            val data = datas[position]
            val isCheck = isSelected(data)
            //如果不允许点击item取消选中状态,则不执行取消操作
            if (!isAllowCancel && isCheck) return@setOnClickListener
            clickCheck(holder as? SmartHolder<VB>, !isCheck, position, it)
        }
    }

    private val onListChangedCallback =
        object : ObservableList.OnListChangedCallback<MutableList<D>>() {
            override fun onChanged(sender: MutableList<D>) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    selectedCache.removeIf { !sender.contains(it) }
                } else {
                    selectedCache.removeAll(selectedCache.filter { !sender.contains(it) }.toSet())
                }
            }

            override fun onItemRangeChanged(
                sender: MutableList<D>, positionStart: Int, itemCount: Int
            ) {

            }

            override fun onItemRangeInserted(
                sender: MutableList<D>, positionStart: Int, itemCount: Int
            ) {

            }

            override fun onItemRangeMoved(
                sender: MutableList<D>, fromPosition: Int, toPosition: Int, itemCount: Int
            ) {

            }

            override fun onItemRangeRemoved(
                sender: MutableList<D>, positionStart: Int, itemCount: Int
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    selectedCache.removeIf { !sender.contains(it) }
                } else {
                    selectedCache.removeAll(selectedCache.filter { !sender.contains(it) }.toSet())
                }
            }
        }


    override val selectedCache: SelectedList<D> by lazy { SelectedList() }
    override var selectedAllChanges: (Employer.(isSelectAll: Boolean) -> Unit)? = null
    override var selectedIndexChange: (Employer.(data: D, position: Int, index: Int) -> Unit)? =
        null
    override var selectedListener: Pair<Int?, Employer.(holder: SmartHolder<VB>?, data: D, position: Int, index: Int, view: View?) -> Unit>? =
        null
    override var borderCall: (Employer.(count: Int) -> Unit)? = null
    override var MAX_CHECK_COUNT: Int? = null
    override var isAllowCancel: Boolean = true
    private var oldSelectedAllStatus: Boolean? = null


//    internal fun addOnListChangedCallback(dataImpl: SmartAdapter<*, D>) {
//
//        dataImpl.addOnListChangedCallback(onListChangedCallback)
//    }
//
//    internal fun removeOnListChangedCallback(dataImpl: SmartAdapter<*, D>) {
//        dataImpl.removeOnListChangedCallback(onListChangedCallback)
//    }

    /*******************************************  核心方法  ******************************************************/
    override fun setOnSelectedListener(
        id: Int?,
        listener: Employer.(holder: SmartHolder<VB>?, data: D, position: Int, index: Int, view: View?) -> Unit
    ): Employer {
        selectedListener = Pair(id, listener)
        return employer
    }

    override fun setOnSelectedAllChange(listener: Employer.(isCheckAll: Boolean) -> Unit): Employer {
        selectedAllChanges = listener
        return employer
    }

    override fun setOnSelectedIndexChange(listener: (Employer.(data: D, position: Int, index: Int) -> Unit)?): Employer {
        selectedIndexChange = listener
        return employer
    }

    override fun setMaxSelectedCount(
        count: Int, borderCall: (Employer.(count: Int) -> Unit)?
    ): Employer {
        this.borderCall = borderCall
        MAX_CHECK_COUNT = count
        return employer
    }

    override fun allowCancel(isAllowCancel: Boolean): Employer {
        this.isAllowCancel = isAllowCancel
        return employer
    }

    override fun setSelected(position: Int, trigger: View?): Employer {
        setSelected(datas[position], trigger)
        return employer
    }

    override fun setSelected(data: D, trigger: View?): Employer {
        if (!isSelected(data)) setCheck(data, null, null)
        return employer
    }

    override fun cancelSelected(position: Int, trigger: View?): Employer {
        cancelSelected(datas[position], trigger)
        return employer
    }

    override fun cancelSelected(data: D, trigger: View?): Employer {
        if (isSelected(data)) cancelCheck(data, null, null)
        return employer
    }

    override fun isSelectAll(): Boolean {
        val providerData = getProviderData()
        return if (selectedCache.size == providerData.size) true
        else {
            selectedCache.containsAll(providerData)
        }
    }

    private fun getProviderData(): List<D> {
        val itemViewType = provider.getItemViewType()
        return datas.filterIndexed { index, d ->
            adapter.getItemViewType(index) == itemViewType
        }
    }

    override fun selectAll(): Employer {
        if (isSelectAll()) return employer//如果已经全选了，则无需执行后续操作
        val providerData = getProviderData()
        selectedCache.addAll(providerData)
        adapter.notifyItemRangeChanged(0, datas.size)
        datas.forEachIndexed { position, data ->
            val indexOf = selectedCache.indexOf(data)
            notifyCheckChanges(null, data, position, indexOf, null)
            notifyCheckIndexChanges(data, position, indexOf)
        }
        return employer
    }

    override fun unselectAll(): Employer {
        if (selectedCache.isEmpty()) return employer //如果在这之前一个元素都没有选择，则无需执行后续操作
        selectedCache.clear()
        adapter.notifyItemRangeChanged(0, datas.size)
        datas.forEachIndexed { position, data ->
            val indexOf = selectedCache.indexOf(data)
            notifyCheckChanges(null, data, position, indexOf, null)
            notifyCheckIndexChanges(data, position, indexOf)
        }
        return employer
    }

    override fun getSelectedDatas(): MutableList<D> = selectedCache.toMutableList()
    override fun isSelected(position: Int) = isSelected(datas[position])
    override fun isSelected(data: D) = selectedCache.contains(data)
    override fun getSelectedIndex(position: Int) = getSelectedIndex(datas[position])

    override fun getSelectedIndex(data: D) = selectedCache.indexOf(data)


    /*******************************************  辅助方法  ******************************************************/
    //取消选中
    open protected fun cancelCheck(
        data: D, holder: SmartHolder<VB>?, view: View?
    ) {
        //必须在删除前拿到被删除item索引
        val indexOf = selectedCache.indexOf(data)
        selectedCache.remove(data)
        //更新被取消选中的item
        datas.forEachIndexed { position, d ->
            if (d == data) {
                adapter.notifyItemChanged(position)
                notifyCheckChanges(holder, d, position, -1, view)
                notifyCheckIndexChanges(d, position, -1)
            }
        }
        if (selectedIndexChange == null) return//如果未设置监听，则不更新，减少不必要的性能损耗
        //找到被取消选中的item后面的所有item，并更新
        val filter = selectedCache.filterIndexed { index, _ -> index >= indexOf }
        datas.forEachIndexed { position, d ->
            if (filter.contains(d)) {
                adapter.notifyItemChanged(position)
                notifyCheckIndexChanges(d, position, selectedCache.indexOf(data))
            }
        }
    }

    //设置为选中
    open protected fun setCheck(
        data: D, holder: SmartHolder<VB>?, view: View?
    ) {
        //当超出最大可选数时，如果设置了越界监听，则不在执行后续操作，否则删除第一个元素后继续追加
        if (selectedCache.size >= (MAX_CHECK_COUNT ?: Int.MAX_VALUE)) if (borderCall == null) {
            while (selectedCache.size >= (MAX_CHECK_COUNT ?: Int.MAX_VALUE)) {
                val get = selectedCache[0]
                if (get != null) cancelCheck(get, null, null)
            }
        } else {
            borderCall?.invoke(employer, selectedCache.size)
            return
        }

        selectedCache.add(data)
        val indexOf = selectedCache.indexOf(data)
        //更新被选中的item
        datas.forEachIndexed { position, d ->
            if (d == data) {
                adapter.notifyItemChanged(position)
                notifyCheckChanges(holder, d, position, indexOf, view)
                notifyCheckIndexChanges(d, position, indexOf)
            }
        }
    }

    //点击事件触发选中或取消
    open protected fun clickCheck(
        holder: SmartHolder<VB>?, isCheck: Boolean, position: Int, view: View?
    ) {
        val data = datas[position] ?: return
        if (isCheck) {
            setCheck(data, holder, view)
        } else {
            cancelCheck(data, holder, view)
        }
    }

    //通知更新选中状态改变
    open protected fun notifyCheckChanges(
        holder: SmartHolder<VB>?, data: D, position: Int, index: Int, view: View?
    ) {
        selectedListener?.second?.invoke(employer, holder, data, position, index, view)
        val isSelectAll = isSelectAll()
        if (oldSelectedAllStatus != isSelectAll) selectedAllChanges?.invoke(employer, isSelectAll)
        oldSelectedAllStatus = isSelectAll
    }

    //通知更新选中索引变化
    open protected fun notifyCheckIndexChanges(data: D, position: Int, index: Int) {
        selectedIndexChange?.invoke(employer, data, position, index)
    }


    fun <T> LinkedHashSet<T?>.removeAt(@IntRange(from = 0) index: Int) {
        var t: T? = null
        this.forEachIndexed { i, data ->
            if (index == i) t = data
            return@forEachIndexed
        }
        this.remove(t)
    }

    fun <T> LinkedHashSet<T>.get(@IntRange(from = 0) index: Int): T? {
        var t: T? = null
        this.forEachIndexed { i, data ->
            if (index == i) t = data
            return@forEachIndexed
        }
        return t
    }

}