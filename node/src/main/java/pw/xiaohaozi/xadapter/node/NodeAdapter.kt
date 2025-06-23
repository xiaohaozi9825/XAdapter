package pw.xiaohaozi.xadapter.node

import android.annotation.SuppressLint
import android.util.Log
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.ext.removeRange
import pw.xiaohaozi.xadapter.smart.impl.EventImpl
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer

/**
 * NodeAdapter
 * 描述：该类主要用于提供多级列表
 * 主要功能：1、子列表的展开与折叠
 *         2、子列表的增删改
 * 注意：数据在展现过程中，会做扁平化处理，涉及到很多递归操作，对性能有一定影响，适合数据量少的场景
 *
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/12/25 10:58
 */
open class NodeAdapter<VB : ViewBinding, D : NodeEntity<*, *>>(
    val eventProxy: EventProxy<NodeAdapter<VB, D>, VB, D> = EventImpl(),//
) : XAdapter<VB, D>(),
    XEmployer, //宿主
    EventProxy<NodeAdapter<VB, D>, VB, D> by eventProxy {
    val TAG = "NodeAdapter"

    init {
        initProxy()
    }

    private fun initProxy() {
        initProxy(this)
    }

    override var employer: NodeAdapter<VB, D>
        get() = this
        set(value) {
            throw XAdapterException("employer不允许设置")
        }

    final override fun initProxy(employer: NodeAdapter<VB, D>) {
        eventProxy.initProxy(employer)
    }

    override fun getEmployerAdapter(): XAdapter<VB, D> {
        return this
    }

    //源数据
    var source: MutableList<D>? = null
        private set


    /**********************************************************************************************************/
    /***********************************     数据操作    ******************************************************/
    /**********************************************************************************************************/

    /**
     * 更新数据
     */
    fun <L : Collection<D>> refresh(list: L) {
        source = list.toMutableList()
        refresh()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        val temp = source?.flattenAndAssociationNode() ?: return
        getDataList().clear()
        getDataList().addAll(temp)
        notifyDataSetChanged()
    }

    /**
     * 增加node
     * @param node
     * @param index 指定位置(相对数据源)，null 在末尾添加
     */
    fun addNode(node: D, index: Int? = null) {
        if (index == null) source?.add(node) else source?.add(index, node)
        val flatten = node.flattenAndAssociationNode()
        val startIndex = if (index == null) getDataList().size else findAdapterPosition(node)
        if (startIndex < 0 || startIndex > itemCount) return
        getDataList().addAll(startIndex, flatten)
        notifyItemRangeInserted(startIndex, flatten.size)
    }


    /**
     * 增加node
     * @param node
     */
    fun <L : Collection<D>> addNode(nodes: L, index: Int? = null) {
        if (nodes.isEmpty()) return
        if (index == null) source?.addAll(nodes) else source?.addAll(index, nodes)
        val flatten = nodes.flattenAndAssociationNode()
        val startIndex = if (index == null) getDataList().size else findAdapterPosition(nodes.first())
        if (startIndex < 0 || startIndex > itemCount) return
        getDataList().addAll(startIndex, flatten)
        notifyItemRangeInserted(startIndex, flatten.size)
    }


    /**
     * 在子节点中增加node
     * @param index 相对与根节点
     * @param node
     */
    fun addChildNode(parent: D, node: D, index: Int? = null) {
        val childList = parent.getChildNodeEntityList() as? MutableList<D> ?: return
        val parentIndex = getDataList().indexOf(parent)
        if (index == null) childList.add(node) else childList.add(index, node)
        //如果父节点未展示，则无需刷新UI，为减少递归，这里提前判断
        val startIndex = if (parentIndex < 0) -1
        else findAdapterPosition(node)
        Log.i(TAG, "addChildNode: startIndex = $startIndex")
        //不符合条件，则不刷新UI
        if (startIndex < 0 || startIndex > itemCount) return
        val flatten = node.flattenAndAssociationNode(parent, parent.getNodeEntityGrade() + 1)
        getDataList().addAll(startIndex, flatten)
        notifyItemRangeInserted(startIndex, flatten.size)
    }

    /**
     * 在子节点中增加node
     * @param index 相对与根节点
     * @param nodes
     */
    fun <L : Collection<D>> addChildNode(parent: D, nodes: L, index: Int? = null) {
        val childList = parent.getChildNodeEntityList() as? MutableList<D> ?: return
        val parentIndex = getDataList().indexOf(parent)
        if (index == null) childList.addAll(nodes) else childList.addAll(index, nodes)
        //如果父节点未展示，则无需刷新UI，为减少递归，这里提前判断
        val startIndex = if (parentIndex < 0) -1
        else findAdapterPosition(nodes.first())
        //不符合条件，则不刷新UI
        if (startIndex < 0 || startIndex > itemCount) return
        val flatten = nodes.flattenAndAssociationNode()
        getDataList().addAll(startIndex, flatten)
        notifyItemRangeInserted(startIndex, flatten.size)
    }


    fun removeNode(node: D) {
        val flatten = node.flatten { if (it is ExpandedNodeEntity) it.isExpanded() else true }
        val parent = node.getParentNodeEntity() as? NodeEntity<*, *>
        if (parent == null) source?.remove(node)
        else parent.getChildNodeEntityList()?.remove(node)
        val position = getDataList().indexOf(node)
        if (getDataList().removeAll(flatten)) {
            if (position >= 0) {
                notifyItemRangeRemoved(position, flatten.size)
            }
        }
    }

    fun removeNodeAt(index: Int) {
        val node = source?.get(index) ?: return
        removeNode(node)
    }

    fun removeNode(start: Int, count: Int) {
        for (index in start until start + count) {
            removeNodeAt(start)
        }
    }


    fun removeNodeList(nodes: List<D>) {
        //由于我们很难确定nodes中元素在recyclerView中是否是连续的，因此采用逐个删除的方法
        nodes.forEach {
            removeNode(it)
        }
    }

    fun removeChildNode(parent: D, node: D) {
        val flatten = node.flatten { if (it is ExpandedNodeEntity) it.isExpanded() else true }
        parent.getChildNodeEntityList()?.remove(node)
        val position = getDataList().indexOf(node)
        if (getDataList().removeAll(flatten)) {
            if (position >= 0) {
                notifyItemRangeRemoved(position, flatten.size)
            }
        }
    }

    fun removeChildNodeAt(parent: D, index: Int) {
        val node = parent.getChildNodeEntityList()?.get(index) as? D ?: return
        removeChildNode(parent, node)
    }

    fun removeChildNode(parent: D, start: Int, count: Int) {
        for (index in start until start + count) {
            removeChildNodeAt(parent, start)
        }
    }

    fun removeChildNodeList(parent: D, nodes: List<D>) {
        //由于我们很难确定nodes中元素在recyclerView中是否是连续的，因此采用逐个删除的方法
        nodes.forEach {
            removeChildNode(parent, it)
        }
    }

    fun removeNodePosition(position: Int) {
        val node = getDataList()[position] as? D ?: return
        val parent = node.getParentNodeEntity() as? D
        if (parent == null) removeNode(node)
        else removeChildNode(parent, node)
    }

    /**
     * 更新数据
     * 如果只是node属性变更，可以调用该方法同步刷新UI
     */
    fun updateNode(node: D, payload: Any? = null) {
        updateNode(node, node, payload)
    }

    /**
     * 更新数据
     * 可以是任意节点数据
     * 该方法只更新当前数据，不会同步刷新子节点数据
     * @param oldNode 旧数据
     * @param newNode 新数据
     */
    fun updateNode(oldNode: D, newNode: D, payload: Any? = null) {
        val parent = oldNode.getParentNodeEntity() as? D
        //如果是一级目录，则更新源数据；否则更新子节点
        if (parent == null) {
            val dataList = getDataList()
            val position = dataList.indexOf(oldNode)//在recyclerView中的位置
            val childIndex = source?.indexOf(oldNode) ?: return//在子节点中的位置
            source!![childIndex] = newNode//交换数据
            if (position >= 0 && position < dataList.size) {
                dataList[position] = newNode
                notifyItemChanged(position, payload)
            }
        } else {
            updateChildNode(parent, oldNode, newNode, payload)
        }
    }

    /**
     * 更新子节点数据
     *
     * 该方法只更新当前数据，不会同步刷新子节点数据
     * @param oldNode 旧数据
     * @param newNode 新数据
     */
    fun updateChildNode(parent: D, oldNode: D, newNode: D, payload: Any? = null) {
        val childList = parent.getChildNodeEntityList() as? MutableList<D> ?: return
        val dataList = getDataList()
        val position = dataList.indexOf(oldNode)//在recyclerView中的位置
        val childIndex = childList.indexOf(oldNode)//在子节点中的位置
        if (parent != null && (newNode as? NodeEntity<NodeEntity<*, *>, *> != null)) newNode.xParentNodeEntity = parent
        childList[childIndex] = newNode//交换数据
        if (position >= 0 && position < dataList.size) {
            dataList[position] = newNode
            notifyItemChanged(position, payload)
        }
    }

    /**
     * 交换数据
     * 该方法会对所有子节点更新
     *
     */
    fun replaceNode(oldNode: D, newNode: D) {
        val parent = oldNode.getParentNodeEntity() as? D
        //如果是一级目录，则更新源数据；否则更新子节点
        if (parent == null) {
            val childIndex = source?.indexOf(oldNode) ?: return//在子节点中的位置
            if (childIndex > -1) {
                removeNode(oldNode)
                addNode(newNode, childIndex)
            }
        } else {
            val childIndex = parent.getChildNodeEntityList()?.indexOf(oldNode) ?: return//在子节点中的位置
            if (childIndex > -1) {
                removeChildNode(parent, oldNode)
                addChildNode(parent, newNode, childIndex)
            }
        }
    }

    /**
     * 查找当前元素在Adapter中理论上所在的位置。
     * 此方法是通过对源数据扁平化计算得出来的，即使未添加到adapter.datas中也能计算；但该方法对性能有一定损耗。
     * 如果确定该node已经存在adapter中，可以使用getData().indexOf()方法获取所在位置。
     * @param node 需要查找的元素
     * @return 在Adapter中对应的position，-1表示不在列表中
     */
    fun findAdapterPosition(node: D): Int {
        return source?.flatten { if (it is ExpandedNodeEntity) it.isExpanded() else true }?.indexOf(node) ?: -1
    }
    /**********************************************************************************************************/
    /*********************************     展开与收起    ******************************************************/
    /**********************************************************************************************************/
    /**
     * 展开
     * @param node 需要展开的节点
     * @param isChangeChildExpand 是否更改所有子节点的展开状态
     */
    fun expand(isChangeChildExpand: Boolean = false) {
        source?.forEach { node ->
            (node as? ExpandedNodeEntity)?.xIsExpanded = true
            if (isChangeChildExpand) {
                node.changeChildExpandStatus()
            }
        }

        refresh()
    }

    /**
     * 收起
     * @param node 需要收起的节点
     * @param isChangeChildExpand 是否更改所有子节点展开状态
     */
    fun collapse(isChangeChildExpand: Boolean = false) {
        source?.forEach { node ->
            (node as? ExpandedNodeEntity)?.xIsExpanded = false
            if (isChangeChildExpand) {
                node.changeChildExpandStatus()
            }
        }
        refresh()
    }

    /**
     * 展开
     * @param position 需要展开的节点
     * @param isExpandChild 是否展开所有子元素
     */
    fun expand(position: Int, isExpandChild: Boolean = false, payload: Any? = null) {
        val dataPosition = getDataPosition(position)
        //拿到对应的节点
        val node = getDataList()[dataPosition]
        //将该节点设置为收起状态
        (node as? ExpandedNodeEntity)?.xIsExpanded = true
        //获取当前节点下的子节点
        val childList = node.getChildNodeEntityList() as? List<D>
        //子节点扁平化处理，得到被收起的所有节点
        val flatten = childList?.flatten {
            return@flatten if (isExpandChild) true
            else {
                if (it is ExpandedNodeEntity) it.isExpanded() else true
            }
        }
        //如果同步更新子节点状态，则遍历所有子节点，并更改状态
        if (isExpandChild) {
            flatten?.forEach { (it as? ExpandedNodeEntity)?.xIsExpanded = true }
        }

        //统计被收起的节点数量
        val count = flatten?.size ?: 0
        //更新当前节点,position 对应adapterPosition
        notifyItemChanged(position, payload)
        if (count > 0) {
            //列表中移除需要收起的节点
            getDataList().addAll(dataPosition + 1, flatten ?: return)
            //更新列表
            notifyItemRangeInserted(position + 1, count)
        }
    }

    /**
     * 收起
     * @param position 需要收起的节点，对应adapterPosition
     * @param isCollapseChild 是否将子元素的状态都置为收起
     */
    fun collapse(position: Int, isCollapseChild: Boolean = false, payload: Any? = null) {
        val dataPosition = getDataPosition(position)
        //拿到对应的节点
        val node = getDataList()[dataPosition]
        //将该节点设置为收起状态
        (node as? ExpandedNodeEntity)?.xIsExpanded = false
        //获取当前节点下的子节点
        val childList = node.getChildNodeEntityList() as? List<D>
        //子节点扁平化处理，得到被收起的所有节点
        val flatten = childList?.flatten { if (it is ExpandedNodeEntity) it.isExpanded() else true }
        //如果同步更新子节点状态，则遍历所有子节点，并更改状态
        if (isCollapseChild) {
            flatten?.forEach { (it as? ExpandedNodeEntity)?.xIsExpanded = false }
        }
        //统计被收起的节点数量
        val count = flatten?.size ?: 0
        Log.i(TAG, "collapse: count = ${flatten?.size}")
        //更新当前节点,position 对应adapterPosition
        notifyItemChanged(position, payload)
        if (count > 0) {
            //列表中移除需要收起的节点
            getDataList().removeRange(dataPosition + 1, count)
            //更新列表
            notifyItemRangeRemoved(position + 1, count)
        }
    }

    //更改所有子节点的展开状态
    private fun NodeEntity<*, *>.changeChildExpandStatus() {
        val list = getChildNodeEntityList() ?: return
        for (childNode in list) {
            if ((childNode as? NodeEntity<*, *>) != null) {
                (childNode as? ExpandedNodeEntity)?.xIsExpanded = (if (this is ExpandedNodeEntity) this.isExpanded() else true)
                childNode.changeChildExpandStatus()
            }
        }
    }

    //数据扁平化处理并建立父子关联关系
    private fun Collection<D>.flattenAndAssociationNode(
        parent: NodeEntity<*, *>? = null,
        grade: Int = 1
    ): MutableList<D> {
        val temp = mutableListOf<D>()
        for (nodeEntity in this) {
            temp += nodeEntity.flattenAndAssociationNode(parent, grade)
        }
        return temp
    }

    //数据扁平化处理并建立父子关联关系
    private fun D.flattenAndAssociationNode(
        parent: NodeEntity<*, *>? = null,
        grade: Int = 1
    ): MutableList<D> {
        val temp = mutableListOf<D>()
        //将当前节点添加到零时列表中
        temp += this
        //设置节点等级，初始等级为1
        this.xNodeGrade = grade
        //如果有父节点，则将当前节点与父节点建立关系
        if (parent != null && (this as? NodeEntity<NodeEntity<*, *>, *> != null)) this.xParentNodeEntity = parent
        if (if (this is ExpandedNodeEntity) this.isExpanded() else true) {
            //如果有子节点
            val childNodeEntityList = this.getChildNodeEntityList() as? List<D>
            if (!childNodeEntityList.isNullOrEmpty()) {
                //遍历子节点，并将子节点结果赋值到临时列表中
                temp += childNodeEntityList.flattenAndAssociationNode(this, grade + 1)
            }
        }
        return temp
    }

    //数据扁平化处理，不修改元素内容
    //isGrandson:是否对所有后代扁平化处理
    private fun D.flatten(block: (node: D) -> Boolean): MutableList<D> {
        val temp = mutableListOf<D>()//创建一个临时数组
        temp += this//将当前值保存到数组中，不管是否能展开子元素，当前元素都会在数组中返回
        if (block.invoke(this)) {//验证当前元素子元素是否需要扁平化处理
            val childList = this.getChildNodeEntityList() as? MutableList<D>
            if (childList != null) {
                temp += childList.flatten(block)
            }
        }
        return temp
    }


    private fun Collection<D>.flatten(block: (node: D) -> Boolean): MutableList<D> {
        val temp = mutableListOf<D>()
        for (childNode in this) {//遍历列表，得到子节点
            //将当前节点添加到零时列表中
            temp += childNode
            if (block.invoke(childNode)) {//如果需要扁平化所有后代
                val childNodeEntityList = childNode.getChildNodeEntityList() as? List<D>
                if (!childNodeEntityList.isNullOrEmpty()) {
                    temp += childNodeEntityList.flatten { block.invoke(it) }
                }
            }
        }
        return temp
    }
}