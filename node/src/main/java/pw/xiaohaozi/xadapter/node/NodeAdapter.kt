package pw.xiaohaozi.xadapter.node

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
 *
 * 描述：
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
    fun <L : Collection<D>> refresh(list: L) {
        source = list.toMutableList()
        refresh()
    }

    fun refresh() {
        val temp = source?.flattenAndAssociationNode() ?: return
        getData().clear()
        getData().addAll(temp)
        notifyDataSetChanged()
    }

    /**
     * 增加node
     * @param node
     * @param index 指定位置，null 在末尾添加
     */
    fun addNode(node: D, index: Int? = null) {
        if (index == null) source?.add(node) else source?.add(index, node)
        val flatten = node.flattenAndAssociationNode()
        val startIndex = if (index == null) getData().size else findAdapterPosition(node)
        if (startIndex < 0 || startIndex > itemCount) return
        getData().addAll(startIndex, flatten)
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
        val startIndex = if (index == null) getData().size else findAdapterPosition(nodes.first())
        if (startIndex < 0 || startIndex > itemCount) return
        getData().addAll(startIndex, flatten)
        notifyItemRangeInserted(startIndex, flatten.size)
    }


    /**
     * 在子节点中增加node
     * @param index 相对与根节点
     * @param node
     */
    fun addChildNode(parent: D, node: D, index: Int? = null) {
        val childList = parent.getChildNodeEntityList() as? MutableList<D> ?: return
        val parentIndex = getData().indexOf(parent)
        if (index == null) childList.add(node) else childList.add(index, node)
        //如果父节点未展示，则无需刷新UI，为减少递归，这里提前判断
        val startIndex = if (parentIndex < 0) -1
        else findAdapterPosition(node)
        //不符合条件，则不刷新UI
        if (startIndex < 0 || startIndex > itemCount) return
        val flatten = node.flattenAndAssociationNode(parent, parent.getNodeEntityGrade() + 1)
        getData().addAll(startIndex, flatten)
        notifyItemRangeInserted(startIndex, flatten.size)
    }

    /**
     * 在子节点中增加node
     * @param index 相对与根节点
     * @param nodes
     */
    fun <L : Collection<D>> addChildNode(parent: D, nodes: L, index: Int? = null) {
        val childList = parent.getChildNodeEntityList() as? MutableList<D> ?: return
        val parentIndex = getData().indexOf(parent)
        if (index == null) childList.addAll(nodes) else childList.addAll(index, nodes)
        //如果父节点未展示，则无需刷新UI，为减少递归，这里提前判断
        val startIndex = if (parentIndex < 0) -1
        else findAdapterPosition(nodes.first())
        //不符合条件，则不刷新UI
        if (startIndex < 0 || startIndex > itemCount) return
        val flatten = nodes.flattenAndAssociationNode()
        getData().addAll(startIndex, flatten)
        notifyItemRangeInserted(startIndex, flatten.size)
    }


    fun removeNode(node: D) {
        val flatten = node.flatten { if (it is ExpandedNodeEntity) it.isExpanded() else true }
        Log.i(TAG, "removeNode: parent = ${node.getParentNodeEntity()}")
        val parent = node.getParentNodeEntity() as? NodeEntity<*, *>
        if (parent == null) source?.remove(node)
        else parent.getChildNodeEntityList()?.remove(node)
        val position = getData().indexOf(node)
        if (getData().removeAll(flatten)) {
            if (position >= 0) {
                notifyItemRangeRemoved(position, flatten.size)
            }
        }
    }

    fun removeNodeAt(index: Int) {
        val node = source?.get(index) as? D ?: return
        removeNode(node)
    }

    fun removeNode(start: Int, count: Int) {
        for (index in start until start + count) {
            removeNodeAt(start)
        }
    }

    fun removeNodeList(nodes: List<D>, continuous: Boolean = true) {
        val flatten = nodes.flatten { if (it is ExpandedNodeEntity) it.isExpanded() else true }
        val node = nodes.first()
        val parent = node.getParentNodeEntity() as? NodeEntity<NodeEntity<*, *>, *>
        if (parent == null) source?.removeAll(nodes)
        else parent.getChildNodeEntityList()?.removeAll(nodes)
        source?.removeAll(nodes)
        val position = getData().indexOf(node)
        if (getData().removeAll(flatten)) {
            if (position >= 0) {
                if (continuous) notifyItemRangeRemoved(position, flatten.size)
                else notifyDataSetChanged()
            }
        }
    }

    fun removeChildNode(parent: D, node: D) {
        val flatten = node.flatten { if (it is ExpandedNodeEntity) it.isExpanded() else true }
        parent.getChildNodeEntityList()?.remove(node)
        val position = getData().indexOf(node)
        if (getData().removeAll(flatten)) {
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

    fun removeChildNodeList(parent: D, nodes: List<D>, continuous: Boolean = true) {
        val flatten = nodes.flatten { if (it is ExpandedNodeEntity) it.isExpanded() else true }
        parent.getChildNodeEntityList()?.removeAll(nodes)
        val position = getData().indexOf(nodes.first())
        if (getData().removeAll(flatten)) {
            if (position >= 0) {
                if (continuous) notifyItemRangeRemoved(position, flatten.size)
                else notifyDataSetChanged()
            }
        }
    }

    fun removeNodePosition(position: Int) {
        val node = getData()[position] as? D ?: return
        val parent = node.getParentNodeEntity() as? D
        if (parent == null) removeNode(node)
        else removeChildNode(parent, node)
    }

    fun updateNode(oldNode: NodeEntity<*, *>, newNode: NodeEntity<*, *>) {}
    fun updateNode(index: Int, newNode: NodeEntity<*, *>) {}
    fun updateChildNode(parent: NodeEntity<*, *>, oldNode: NodeEntity<*, *>, newNode: NodeEntity<*, *>) {}
    fun updateChildNode(parent: NodeEntity<*, *>, index: Int, newNode: NodeEntity<*, *>) {}

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
                node.changeChildExpand()
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
                node.changeChildExpand()
            }
        }
        refresh()
    }

    /**
     * 展开
     * @param position 需要展开的节点
     * @param isChangeChildExpand 是否更改所有子节点的展开状态
     */
    fun expand(position: Int, isChangeChildExpand: Boolean = false, payload: Any? = null) {
        val dataPosition = getDataPosition(position)
        //拿到对应的节点
        val node = getData()[dataPosition]
        //将该节点设置为收起状态
        (node as? ExpandedNodeEntity)?.xIsExpanded = true
        //获取当前节点下的子节点
        val childList = node.getChildNodeEntityList() as? List<D>
        //子节点扁平化处理，得到被收起的所有节点
        val flatten = childList?.flatten {
            return@flatten if (isChangeChildExpand) true
            else {
                if (it is ExpandedNodeEntity) it.isExpanded() else true
            }
        }
        //如果同步更新子节点状态，则遍历所有子节点，并更改状态
        if (isChangeChildExpand) {
            flatten?.forEach { (it as? ExpandedNodeEntity)?.xIsExpanded = true }
        }

        //统计被收起的节点数量
        val count = flatten?.size ?: 0
        //更新当前节点,position 对应adapterPosition
        notifyItemChanged(position, payload)
        //列表中移除需要收起的节点
        getData().addAll(dataPosition + 1, flatten ?: return)
        //更新列表
        notifyItemRangeInserted(position + 1, count)
    }

    /**
     * 收起
     * @param position 需要收起的节点，对应adapterPosition
     * @param isChangeChildExpand 是否更改所有子节点展开状态。该字段只是标记状态，展开时使用。收起时不管填true还是false，元素都会从列表中移除
     */
    fun collapse(position: Int, isChangeChildExpand: Boolean = false, payload: Any? = null) {
        val dataPosition = getDataPosition(position)
        //拿到对应的节点
        val node = getData()[dataPosition]
        //将该节点设置为收起状态
        (node as? ExpandedNodeEntity)?.xIsExpanded = false
        //获取当前节点下的子节点
        val childList = node.getChildNodeEntityList() as? List<D>
        //子节点扁平化处理，得到被收起的所有节点
        val flatten = childList?.flatten { if (it is ExpandedNodeEntity) it.isExpanded() else true }
        //如果同步更新子节点状态，则遍历所有子节点，并更改状态
        if (isChangeChildExpand) {
            flatten?.forEach { (it as? ExpandedNodeEntity)?.xIsExpanded = true }
        }
        //统计被收起的节点数量
        val count = flatten?.size ?: 0
        Log.i(TAG, "collapse: count = ${flatten?.size}")
        //更新当前节点,position 对应adapterPosition
        notifyItemChanged(position, payload)
        //列表中移除需要收起的节点
        getData().removeRange(dataPosition + 1, count)
        //更新列表
        notifyItemRangeRemoved(position + 1, count)
    }

    //更改所有子节点的展开状态
    private fun NodeEntity<*, *>.changeChildExpand() {
        val list = getChildNodeEntityList() ?: return
        for (childNode in list) {
            if ((childNode as? NodeEntity<*, *>) != null) {
                (childNode as? ExpandedNodeEntity)?.xIsExpanded = (if (this is ExpandedNodeEntity) this.isExpanded() else true)
                childNode.changeChildExpand()
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
        val temp = mutableListOf<D>()
        temp += this
        val childList = this.getChildNodeEntityList() as? MutableList<D>
        if (childList != null) {
            temp += childList.flatten(block)
        }
        return temp
    }

    //数据扁平化处理，不修改元素内容
    //isGrandson:是否对所有后代扁平化处理
    private fun Collection<D>.flatten(isGrandson: Boolean): MutableList<D> {
        val temp = mutableListOf<D>()
        for (childNode in this) {//遍历列表，得到子节点
            //将当前节点添加到零时列表中
            temp += childNode
            if (isGrandson) {//如果需要扁平化所有后代
                val childNodeEntityList = childNode.getChildNodeEntityList() as? List<D>
                if (!childNodeEntityList.isNullOrEmpty()) {
                    temp += childNodeEntityList.flatten(true)
                }
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
                    temp += childNodeEntityList.flatten(true)
                }
            }
        }
        return temp
    }
}