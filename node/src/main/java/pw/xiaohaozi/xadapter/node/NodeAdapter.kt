package pw.xiaohaozi.xadapter.node

import android.util.Log
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.ext.removeRange

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/12/25 10:58
 */
open class NodeAdapter<VB : ViewBinding> : XAdapter<VB, NodeEntity<*, *>>() {
    val TAG = "NodeAdapter"

    //源数据
    var source: Collection<NodeEntity<*, *>>? = null
    fun <L : Collection<NodeEntity<*, *>>> refresh(list: L) {
        source = list
        refresh()
    }

    fun refresh() {
        val temp = source?.flattenAndAssociationNode() ?: return
        getData().clear()
        getData().addAll(temp)
        notifyDataSetChanged()
    }

    /**
     * 展开
     * @param node 需要展开的节点
     * @param isChangeChildExpand 是否更改所有子节点的展开状态
     */
    fun expand(isChangeChildExpand: Boolean = false) {
        source?.forEach { node ->
            node.setNodeExpandedStatus(true)
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
            node.setNodeExpandedStatus(false)
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
        node.setNodeExpandedStatus(true)
        //获取当前节点下的子节点
        val childList = node.getChildNodeEntityList() as? List<NodeEntity<*, *>>
        //子节点扁平化处理，得到被收起的所有节点
        val flatten = childList?.flatten {
            return@flatten if (isChangeChildExpand) true
            else {
                it.isNodeExpandedStatus()
            }
        }
        //如果同步更新子节点状态，则遍历所有子节点，并更改状态
        if (isChangeChildExpand) {
            flatten?.forEach { it.setNodeExpandedStatus(true) }
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
        node.setNodeExpandedStatus(false)
        //获取当前节点下的子节点
        val childList = node.getChildNodeEntityList() as? List<NodeEntity<*, *>>
        //子节点扁平化处理，得到被收起的所有节点
        val flatten = childList?.flatten { it.isNodeExpandedStatus() }
        //如果同步更新子节点状态，则遍历所有子节点，并更改状态
        if (isChangeChildExpand) {
            flatten?.forEach { it.setNodeExpandedStatus(false) }
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
                childNode.setNodeExpandedStatus(isNodeExpandedStatus())
                childNode.changeChildExpand()
            }
        }
    }

    //数据扁平化处理并建立父子关联关系
    private fun <Node : NodeEntity<*, *>> Collection<Node>.flattenAndAssociationNode(
        parent: NodeEntity<*, *>? = null,
        grade: Int = 1
    ): MutableList<NodeEntity<*, *>> {
        val temp = mutableListOf<NodeEntity<*, *>>()
        for (nodeEntity in this) {
            //将当前节点添加到零时列表中
            temp += nodeEntity
            //设置节点等级，初始等级为1
            nodeEntity.setNodeEntityGrade(grade)
            //如果有父节点，则将当前节点与父节点建立关系
            if (parent != null && (nodeEntity as? NodeEntity<Any, *> != null)) nodeEntity.setParentNodeEntity(parent)
            if (nodeEntity.isNodeExpandedStatus()) {
                //如果有子节点
                val childNodeEntityList = nodeEntity.getChildNodeEntityList() as? List<NodeEntity<*, *>>
                if (!childNodeEntityList.isNullOrEmpty()) {
                    //遍历子节点，并将子节点结果赋值到临时列表中
                    temp += childNodeEntityList.flattenAndAssociationNode(nodeEntity, grade + 1)
                }
            }
        }
        return temp
    }

    //数据扁平化处理，不修改元素内容
    //isGrandson:是否对所有后代扁平化处理
    private fun <Node : NodeEntity<*, *>> Collection<Node>.flatten(isGrandson: Boolean): MutableList<NodeEntity<*, *>> {
        val temp = mutableListOf<NodeEntity<*, *>>()
        for (childNode in this) {//遍历列表，得到子节点
            //将当前节点添加到零时列表中
            temp += childNode
            if (isGrandson) {//如果需要扁平化所有后代
                val childNodeEntityList = childNode.getChildNodeEntityList() as? List<NodeEntity<*, *>>
                if (!childNodeEntityList.isNullOrEmpty()) {
                    temp += childNodeEntityList.flatten(true)
                }
            }
        }
        return temp
    }

    private fun <Node : NodeEntity<*, *>> Collection<Node>.flatten(block: (node: Node) -> Boolean): MutableList<NodeEntity<*, *>> {
        val temp = mutableListOf<NodeEntity<*, *>>()
        for (childNode in this) {//遍历列表，得到子节点
            //将当前节点添加到零时列表中
            temp += childNode
            if (block.invoke(childNode)) {//如果需要扁平化所有后代
                val childNodeEntityList = childNode.getChildNodeEntityList() as? List<NodeEntity<*, *>>
                if (!childNodeEntityList.isNullOrEmpty()) {
                    temp += childNodeEntityList.flatten(true)
                }
            }
        }
        return temp
    }
}
