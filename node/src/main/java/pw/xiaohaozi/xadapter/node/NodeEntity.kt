package pw.xiaohaozi.xadapter.node

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/12/25 11:22
 */
interface NodeEntity<Parent, Child> {
    //父节点
    var _parentNodeEntity: Parent?
    //级别
    var _nodeGrade: Int?

    /**
     * 获取父节点
     * 如果该节点不是根节点，需要重新该方法。
     */
    fun getParentNodeEntity(): Parent? {
        return _parentNodeEntity
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
        return _nodeGrade ?: -1
    }


}