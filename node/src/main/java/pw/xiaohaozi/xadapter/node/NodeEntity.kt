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
    fun getParentNodeEntity(): Parent?{return null}
    fun setParentNodeEntity(parent: Parent){}
    fun getChildNodeEntityList(): List<Child>?
}