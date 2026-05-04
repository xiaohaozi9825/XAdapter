# Node展开与折叠
> 展开或收起子节点
## 用法

### 方法定义
```kotlin
/**
 * 展开
 * @param position 需要展开的节点
 * @param isExpandChild 是否展开所有子元素
 */
@Suppress("UNCHECKED_CAST")
fun expand(position: Int, isExpandChild: Boolean = false, payload: Any? = null)

/**
 * 收起
 * @param position 需要收起的节点，对应adapterPosition
 * @param isCollapseChild 是否将子元素的状态都置为收起
 */
@Suppress("UNCHECKED_CAST")
fun collapse(position: Int, isCollapseChild: Boolean = false, payload: Any? = null) 
```

### 基础用法
```kotlin
//①数据类定义：实现NodeEntity接口的同时，还需要实现ExpandedNodeEntity接口。
data class ProvinceNode(val name: String, val city: MutableList<CityNode>) : NodeEntity<Unit, CityNode>, ExpandedNodeEntity {
    override var xParentNodeEntity: Unit? = null
    override var xNodeGrade: Int? = null

    @Transient
    override var xIsExpanded: Boolean? = null

    override fun isExpanded(): Boolean {
        return xIsExpanded ?: true
    }

    override fun getChildNodeEntityList(): MutableList<CityNode> {
        return city
    }
}

data class CityNode(val name: String, val area: ArrayList<AreaNode>) : NodeEntity<ProvinceNode, AreaNode>, ExpandedNodeEntity {
    override var xParentNodeEntity: ProvinceNode? = null
    override var xNodeGrade: Int? = null

    @Transient
    override var xIsExpanded: Boolean? = true

    override fun getChildNodeEntityList(): MutableList<AreaNode> {
        return area
    }
}

data class AreaNode(val name: String) : NodeEntity<CityNode, Unit> {
    override var xParentNodeEntity: CityNode? = null
    override var xNodeGrade: Int? = null
    override fun getChildNodeEntityList(): MutableList<Unit>? {
        return null
    }
}

//②点击触发展开与收起操作
adapter.setOnClickListener { holder, data, position, view ->
    if (data.isExpanded()) adapter.collapse(position, false)
    else adapter.expand(position, false)
}
```
