package pw.xiaohaozi.xadapter.node.entity

/**
 * 树形节点实体：描述父子关系、层级与子节点列表；与 [NodeAdapter] 扁平化展示配合使用。
 *
 * 描述：[Parent]、[Child] 分别约束父节点与列表中子节点类型；层级可通过 [xNodeGrade] 或重写 [getNodeEntityGrade] 维护。
 * 作者：小耗子
 * 创建时间：2024/12/25 11:22
 */
interface NodeEntity<Parent, Child> {
    /** 父节点引用；根节点应为 null，由 [NodeAdapter] 在扁平化时维护。 */
    var xParentNodeEntity: Parent?
    /** 节点层级；未赋值时 [getNodeEntityGrade] 返回 -1。 */
    var xNodeGrade: Int?

    /**
     * 获取父节点
     * 如果该节点不是根节点，需要重新该方法。
     */
    fun getParentNodeEntity(): Parent? {
        return xParentNodeEntity
    }

    /**
     * 获取子节点列表
     */
    fun getChildNodeEntityList(): MutableList<Child>?


    /**
     * 获取节点级别
     * 如果需要记录节点级别，子类需要重写setNodeEntityGrade()、getNodeEntityGrade()方法，定义一个变量存储节点级别
     * 如果没有重写，则默认-1.如果每个节点对呀不同的实体类，可以不用重写set方法，直接在get方法中返回对应的级别即可。
     */
    fun getNodeEntityGrade(): Int {
        return xNodeGrade ?: -1
    }


}
