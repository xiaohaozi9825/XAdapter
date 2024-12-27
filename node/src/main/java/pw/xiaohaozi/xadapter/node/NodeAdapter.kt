package pw.xiaohaozi.xadapter.node

import android.util.Log
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
    fun <L : Collection<NodeEntity<*, *>>> refresh(list: L){
    val temp=  list.flat()
       getData().addAll(temp)
   }

    fun <Node : NodeEntity<*, *>> Collection<Node>.flat(parent: NodeEntity<*, *>? = null): MutableList<NodeEntity<*, *>> {
        val temp = mutableListOf<NodeEntity<*, *>>()
        for (nodeEntity in this) {
            //将当前节点添加到零时列表中
            temp += nodeEntity
            //如果有父节点，则将当前节点与父节点建立关系
            if (parent != null && (nodeEntity as? NodeEntity<Any, *> != null)) nodeEntity.setParentNodeEntity(parent)
            //如果有子节点
            val childNodeEntityList = nodeEntity.getChildNodeEntityList() as? List<NodeEntity<*, *>>
            if (!childNodeEntityList.isNullOrEmpty()) {
                //遍历子节点，并将子节点结果赋值到临时列表中
                temp += childNodeEntityList.flat(nodeEntity)
            }
        }
        return temp
    }
}

class Sheng(val name: String, val childList: List<Shi>) : NodeEntity<Unit, Shi> {

    override fun getChildNodeEntityList(): List<Shi> {
        return childList
    }

    override fun toString(): String {
        return name
    }
}

class Shi(val name: String, val childList: List<Xian>) : NodeEntity<Sheng, Xian> {
    var parent: Sheng? = null
    override fun getParentNodeEntity(): Sheng? {
        return parent
    }

    override fun getChildNodeEntityList(): List<Xian> {
        return childList
    }

    override fun setParentNodeEntity(parent: Sheng) {
        this.parent = parent
    }
    override fun toString(): String {
        return name
    }
}

class Xian(val name: String) : NodeEntity<Shi, Unit> {
    private var parent: Shi? = null
    override fun getParentNodeEntity(): Shi? {
        return parent
    }

    override fun getChildNodeEntityList(): List<Unit>? {
        return null
    }

    override fun setParentNodeEntity(parent: Shi) {
        this.parent = parent
    }

    override fun toString(): String {
        return name
    }
}