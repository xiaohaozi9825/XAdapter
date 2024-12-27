package pw.xiaohaozi.xadapter.node

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/12/25 10:58
 */
open class NodeAdapter<VB : ViewBinding> : XAdapter<VB, NodeEntity<*, *>>() {
    //源数据
    var source: Collection<NodeEntity<*, *>>? = null
    fun <L : Collection<NodeEntity<*, *>>> refresh(list: L) {
        source = list
        refresh()
    }

    fun refresh() {
        val temp = source?.flatten() ?: return
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
     * @param node 需要展开的节点
     * @param isChangeChildExpand 是否更改所有子节点的展开状态
     */
    fun expand(node: NodeEntity<*, *>, isChangeChildExpand: Boolean = false) {
        node.setNodeExpandedStatus(true)
        if (isChangeChildExpand) {
            node.changeChildExpand()
        }
        refresh()
    }

    /**
     * 收起
     * @param node 需要收起的节点
     * @param isChangeChildExpand 是否更改所有子节点展开状态
     */
    fun collapse(node: NodeEntity<*, *>, isChangeChildExpand: Boolean = false) {
        node.setNodeExpandedStatus(false)
        if (isChangeChildExpand) {
            node.changeChildExpand()
        }
        refresh()
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

    //数据扁平化处理
    fun <Node : NodeEntity<*, *>> Collection<Node>.flatten(
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
                    temp += childNodeEntityList.flatten(nodeEntity, grade + 1)
                }
            }
        }
        return temp
    }
}
