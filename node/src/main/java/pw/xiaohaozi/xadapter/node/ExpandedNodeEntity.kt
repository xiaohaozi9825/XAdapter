package pw.xiaohaozi.xadapter.node

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/12/31 16:15
 */
interface ExpandedNodeEntity {
    var _isExpanded: Boolean


    /**
     * 是否为展开状态
     */
    fun isExpanded(): Boolean {
        return _isExpanded
    }

}