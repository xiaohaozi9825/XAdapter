package pw.xiaohaozi.xadapter.node.entity

/**
 * 可展开/收起的节点：通过 [xIsExpanded] 与 [isExpanded] 控制子树是否参与扁平化。
 *
 * 描述：实现该接口的节点在 [pw.xiaohaozi.xadapter.node.NodeAdapter] 中支持展开、收起等操作。
 * 作者：小耗子
 * 创建时间：2024/12/31 16:15
 */
interface ExpandedNodeEntity {
    /** 是否展开；为 null 时按 false 处理。 */
    var xIsExpanded: Boolean?


    /**
     * 是否为展开状态
     */
    fun isExpanded(): Boolean {
        return xIsExpanded ?: false
    }

}
