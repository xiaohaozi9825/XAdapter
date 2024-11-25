package pw.xiaohaozi.xadapter.smart.impl

import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.smartadapter.utils.SelectedList
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import pw.xiaohaozi.xadapter.smart.proxy.ObservableList
import pw.xiaohaozi.xadapter.smart.proxy.OnItemSelectListener
import pw.xiaohaozi.xadapter.smart.proxy.OnSelectedDataChangesListener
import pw.xiaohaozi.xadapter.smart.proxy.SelectedProxy
import pw.xiaohaozi.xadapter.smart.proxy.SmartDataProxy
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer
import pw.xiaohaozi.xadapter.smart.proxy.XProxy
import kotlin.math.max
import kotlin.math.min

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/5 22:47
 */
open class AdapterSelectedImpl<Employer : XProxy<Employer>, VB : ViewBinding, D> :
    SelectedProxy<Employer, VB, D> {
    override lateinit var employer: Employer

    private val adapter: XAdapter<*, *> by lazy {
        when (val e = employer) {
            is XEmployer -> e.getEmployerAdapter()
            else -> throw NullPointerException("找不到对应的Adapter对象")
        }
    }

    private fun getDatas() = adapter.getData() as MutableList<D>
    override val selectedCache: SelectedList<D> by lazy { SelectedList() }
    override var onSelectedDataChangesListener: OnSelectedDataChangesListener<Employer, D>? = null
    override var itemSelectListener: Triple<Int?, String?, OnItemSelectListener<Employer, D>>? = null
    override var maxSelectCount: Int? = null
    override var isAllowCancel: Boolean = true
    override var isAutoCancel: Boolean = true

    //是否全选
    private var oldSelectedAllStatus: Boolean? = null

    //当前状态是否为全选
    private var curSelectedAllStatus = false

    //参与选择操作的Providers对应的itemType
    private val selectItemTypes by lazy {
        val temp = mutableListOf<Int>()
        adapter.providers.forEach { key, value ->
            if ((value as? SmartProvider<*, *>)?.select == true) temp.add(key)
        }
        return@lazy temp
    }


    private val onListChangedCallback = object : ObservableList.OnListChangedCallback<MutableList<D>>() {
        val TAG = "OnListChangedCallback"

        //刷新所有数据时回调
        override fun onChanged(sender: MutableList<D>) {
            Log.i(TAG, "onChanged: ")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                selectedCache.removeIf { !sender.contains(it) }
            } else {
                selectedCache.removeAll(selectedCache.filter { !sender.contains(it) }.toSet())
            }
            curSelectedAllStatus = false

            val start = 0
            val end = sender.size
            for (position: Int in start until end) {
                val adapterPosition = adapter.getAdapterPosition(position)
                notifyItemSelectedChanges(sender[position], adapterPosition, -1, false)
            }
            notifySelectedDataChanges(false)
        }

        //更新部分数据时回调
        override fun onItemRangeChanged(sender: MutableList<D>, positionStart: Int, itemCount: Int) {
            Log.i(TAG, "onItemRangeChanged: ")
            notifySelectedDataChanges(curSelectedAllStatus)
        }

        //插入数据时回调
        override fun onItemRangeInserted(sender: MutableList<D>, positionStart: Int, itemCount: Int) {
            Log.i(TAG, "onItemRangeInserted: ")
            curSelectedAllStatus = false
            val start = max(positionStart, 0)
            val end = min(positionStart + itemCount, sender.size)
            for (position: Int in start until end) {
                val adapterPosition = adapter.getAdapterPosition(position)
                notifyItemSelectedChanges(sender[position], adapterPosition, -1, false)
            }
            notifySelectedDataChanges(false)
        }

        //移动数据时回调
        override fun onItemRangeMoved(sender: MutableList<D>, fromPosition: Int, toPosition: Int, itemCount: Int) {
            Log.i(TAG, "onItemRangeMoved: ")
        }

        //删除数据时回调
        override fun onItemRangeRemoved(sender: MutableList<D>, changeDatas: MutableList<D>, positionStart: Int, itemCount: Int) {
            Log.i(TAG, "onItemRangeRemoved: ${changeDatas.size}")
            //被删除的元素在selectedCache中的起始索引
            val startIndex = if (positionStart > -1) positionStart
            else selectedCache.indexOfFirst { changeDatas.contains(it) }

            Log.i(TAG, "onItemRangeRemoved: indexOf = ${startIndex}")
            //找到被取消选中的item后面的所有item，并更新
            val filter = selectedCache.filterIndexed { index, _ -> index >= startIndex }
            Log.i(TAG, "onItemRangeRemoved: filter = ${filter.size}")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                selectedCache.removeIf { changeDatas.contains(it) }
            } else {
                selectedCache.removeAll(selectedCache.filter { changeDatas.contains(it) }.toSet())
            }
            curSelectedAllStatus = dpSelectAll()


            getDatas().forEachIndexed { position, d ->
                if (filter.contains(d)) {
                    Log.i(TAG, "notifyItemChanged: position = ${position}")
                    val adapterPosition = adapter.getAdapterPosition(position)
                    if (adapterPosition > -1 && adapterPosition < adapter.itemCount) {
                        adapter.notifyItemChanged(adapterPosition)
                        notifyItemSelectedChanges(d, adapterPosition, -1, false)
                    }
                }
            }
            notifySelectedDataChanges(curSelectedAllStatus)
        }


    }

    override fun initProxy(employer: Employer) {
        super.initProxy(employer)

        adapter.addOnViewHolderChanges(object : XAdapter.OnViewHolderChanges {
            override fun onCreated(provide: TypeProvider<*, *>, holder: XHolder<*>) {
                initListener(holder)
            }

            override fun onBinding(holder: XHolder<*>, position: Int) {

            }

            override fun onBinding(holder: XHolder<*>, position: Int, payloads: List<Any?>) {

            }

        })
        adapter.addOnRecyclerViewChanges(object : XAdapter.OnRecyclerViewChanges {
            override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

            }

            override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
                removeOnListChangedCallback(adapter as SmartDataProxy<*, *, D>)
            }

        })
        addOnListChangedCallback(adapter as SmartDataProxy<*, *, D>)
    }

    internal fun addOnListChangedCallback(dataImpl: SmartDataProxy<*, *, D>) {
        dataImpl.addOnListChangedCallback(onListChangedCallback)
    }

    internal fun removeOnListChangedCallback(dataImpl: SmartDataProxy<*, *, D>) {
        dataImpl.removeOnListChangedCallback(onListChangedCallback)
    }

    private fun initListener(holder: XHolder<*>) {
        val selectedListener = this.itemSelectListener ?: return
        val viewId = selectedListener.first
        val tagger: View = viewId?.let { holder.itemView.findViewById(it) } ?: holder.itemView
        tagger.setOnClickListener {
            val position = adapter.getDataPosition(holder.adapterPosition)
            if (position < 0 || position >= getDatas().size) return@setOnClickListener
            val data = getDatas()[position]
            val isCheck = isSelected(data)
            //如果不允许点击item取消选中状态,则不执行取消操作
            if (!isAllowCancel && isCheck) return@setOnClickListener
            clickCheck(holder as? XHolder<VB>, !isCheck, position)
        }
    }

    /*******************************************  核心方法  ******************************************************/
    override fun setOnItemSelectListener(
        id: Int?,
        payload: String?,
        listener: OnItemSelectListener<Employer, D>
    ): Employer {
        itemSelectListener = Triple(id, payload, listener)
        return employer
    }

    override fun setOnSelectAllListener(listener: OnSelectedDataChangesListener<Employer, D>): Employer {
        onSelectedDataChangesListener = listener
        return employer
    }

    override fun setMaxSelectCount(count: Int): Employer {
        maxSelectCount = count
        return employer
    }

    override fun isAllowCancel(isAllowCancel: Boolean): Employer {
        this.isAllowCancel = isAllowCancel
        return employer
    }

    override fun isAutoCancel(isAutoCancel: Boolean): Employer {
        this.isAutoCancel = isAutoCancel
        return employer
    }

    override fun setSelectAt(position: Int, isSelect: Boolean, fromUser: Boolean): Int {
        return setSelect(getDatas()[position], isSelect, fromUser)
    }

    override fun setSelect(data: D, isSelect: Boolean, fromUser: Boolean): Int {
        return if (isSelect) setCheck(data, fromUser)
        else cancelCheck(data, fromUser)
    }

    override fun isSelectAll(): Boolean {
        return curSelectedAllStatus
    }

    override fun selectAll(): Int {
        if (oldSelectedAllStatus == true) return 0//如果已经全选了，则无需执行后续操作
        val selects = filterSelectTypeDatas()
        if (maxSelectCount != null && selects.size > maxSelectCount!!) {
            for (select in selects) {
                if (selectedCache.size >= maxSelectCount!!) break
                selectedCache.add(select)
            }
        } else {
            if (!selectedCache.addAll(selects)) return 0
            curSelectedAllStatus = true
        }
        //刷新所有数据，头布局和脚布局不刷新
        adapter.notifyItemRangeChanged(
            adapter.getAdapterPosition(0),
            adapter.getAdapterPosition(getDatas().size),
            itemSelectListener?.second
        )
        selects.forEachIndexed { position, data ->
            val indexOf = selectedCache.indexOf(data)
            val adapterPosition = adapter.getAdapterPosition(position)
            notifyItemSelectedChanges(data, adapterPosition, indexOf, false)
        }
        notifySelectedDataChanges(curSelectedAllStatus)
        return selectedCache.size
    }

    override fun deselectAll(): Int {
        if (selectedCache.isEmpty()) return 0 //如果在这之前一个元素都没有选择，则无需执行后续操作
        selectedCache.clear()
        curSelectedAllStatus = false
        //刷新所有数据，头布局和脚布局不刷新
        adapter.notifyItemRangeChanged(
            adapter.getAdapterPosition(0),
            adapter.getAdapterPosition(getDatas().size),
            itemSelectListener?.second
        )
        getDatas().forEachIndexed { position, data ->
            val indexOf = selectedCache.indexOf(data)
            val adapterPosition = adapter.getAdapterPosition(position)
            notifyItemSelectedChanges(data, adapterPosition, indexOf, false)
        }
        notifySelectedDataChanges(curSelectedAllStatus)
        return 0
    }

    override fun getSelectedDatas(): MutableList<D> = selectedCache.toMutableList()
    override fun isSelectedAt(position: Int) = isSelected(getDatas()[position])
    override fun isSelected(data: D) = selectedCache.contains(data)
    override fun getSelectedIndexAt(position: Int) = getSelectedIndex(getDatas()[position])
    override fun getSelectedIndex(data: D) = selectedCache.indexOf(data)


    /*******************************************  辅助方法  ******************************************************/
    //取消选中
    protected open fun cancelCheck(data: D, fromUser: Boolean): Int {
        if (!isAllowCancel) return -1//如果不允许取消，则无法选择，操作失败
        //必须在删除前拿到被删除item索引
        val indexOf = selectedCache.indexOf(data)
        if (indexOf == -1) return 0//如果没有找到该元素，则取消个数为0
        selectedCache.removeAt(indexOf)
        curSelectedAllStatus = false
        //更新被取消选中的item
        getDatas().forEachIndexed { position, d ->
            if (d == data) {
                val adapterPosition = adapter.getAdapterPosition(position)
                if (adapterPosition > -1 && adapterPosition < adapter.itemCount) {
                    adapter.notifyItemChanged(adapterPosition, itemSelectListener?.second)
                    notifyItemSelectedChanges(d, adapterPosition, -1, fromUser)
                }
            }
        }
        //找到被取消选中的item后面的所有item，并更新
        val filter = selectedCache.filterIndexed { index, _ -> index >= indexOf }
        getDatas().forEachIndexed { position, d ->
            if (filter.contains(d)) {
                val adapterPosition = adapter.getAdapterPosition(position)
                if (adapterPosition > -1 && adapterPosition < adapter.itemCount) {
                    adapter.notifyItemChanged(adapterPosition, itemSelectListener?.second)
                    notifyItemSelectedChanges(d, adapterPosition, -1, fromUser)
                }
            }
        }
        notifySelectedDataChanges(curSelectedAllStatus)
        return 1
    }

    //设置为选中
    protected open fun setCheck(data: D, fromUser: Boolean): Int {
        if (maxSelectCount != null) {
            val selectedSize = selectedCache.size
            if (selectedSize >= maxSelectCount!!) {//如果超出了最大选择数
                if (!isAllowCancel) return -1//如果不允许取消，则无法选择，操作失败
                if (!isAutoCancel) return -1//超出范围，不自动取消，也无法选择更多，操作失败
                //超出范围自动取消
                while (selectedCache.size >= (maxSelectCount!!)) {
                    val get = selectedCache.firstOrNull()
                    if (get != null) cancelCheck(get, false)
                }
            }
        }

        if (selectedCache.contains(data)) return 0//如果已选，则不执行操作，选中数量为0
        if (!selectedCache.add(data)) return 0 //如果添加失败，不再执行后续操作，选中数量为0
        val indexOf = selectedCache.indexOf(data)
        curSelectedAllStatus = dpSelectAll()
        //更新被选中的item
        getDatas().forEachIndexed { position, d ->
            if (d == data) {
                val adapterPosition = adapter.getAdapterPosition(position)
                if (adapterPosition > -1 && adapterPosition < adapter.itemCount) {
                    adapter.notifyItemChanged(adapterPosition, itemSelectListener?.second)
                    notifyItemSelectedChanges(d, adapterPosition, indexOf, fromUser)
                }
            }
        }
        notifySelectedDataChanges(curSelectedAllStatus)
        return 1
    }

    //点击事件触发选中或取消
    protected open fun clickCheck(holder: XHolder<VB>?, isCheck: Boolean, position: Int) {
        val data = getDatas()[position] ?: return
        if (isCheck) {
            setCheck(data, true)
        } else {
            cancelCheck(data, true)
        }
    }

    //计算是否全选
    private fun dpSelectAll(): Boolean {
        val selects = filterSelectTypeDatas()
        return if (selectedCache.isEmpty()) false
        else if (selectedCache.size == selects.size) true
        else {
            selectedCache.containsAll(selects)
        }
    }

    //过滤出参与选择事件的数据
    private fun filterSelectTypeDatas(): List<D> {
        return getDatas().filter { data ->
            val adapterPosition = adapter.getAdapterPosition(getDatas().indexOf(data))
            val itemType = adapter.getItemViewType(adapterPosition)
            selectItemTypes.contains(itemType)
        }
    }

    //通知更新选中状态改变
    protected open fun notifyItemSelectedChanges(
        data: D,
        position: Int,
        index: Int,
        fromUser: Boolean
    ) {
        itemSelectListener?.third?.invoke(employer, data, position, index, fromUser)
    }

    //通知更新选中状态改变
    protected open fun notifySelectedDataChanges(isSelectAll: Boolean) {
        if (onSelectedDataChangesListener != null) {
            oldSelectedAllStatus = isSelectAll
            onSelectedDataChangesListener?.invoke(employer, selectedCache, isSelectAll)
        }
    }


}