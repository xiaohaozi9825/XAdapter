# Node创建多布局
> 如省市县联动
## 用法

### 方法定义
```kotlin
//创建NodeAdapter
fun nodeAdapter(
    onItemId: OnItemId<ViewBinding, NodeEntity<*, *>>? = null,
    custom: OnCustomType? = null,
): NodeAdapter<ViewBinding, NodeEntity<*, *>>

//切换多种类型
inline fun <reified vb : VB, reified d : D> withType(
    isFixed: Boolean? = null,
    itemType: Int? = null,
    init: (NodeProvider<VB, D, vb, d>.() -> Unit) = {},
    crossinline create: OnProviderInitHolder<VB, D, vb, d> = {},
    crossinline bind: OnProviderBindHolder<VB, D, vb, d>,
)
```

### 基础用法
```kotlin
//①数据类准备
data class AreaNode(val name: String) : NodeEntity<CityNode, Unit> {
    override var xParentNodeEntity: CityNode? = null
    override var xNodeGrade: Int? = null
    override fun getChildNodeEntityList(): MutableList<Unit>? {
        return null
    }
}

data class ProvinceNode(val name: String, val city: MutableList<CityNode>) : NodeEntity<Unit, CityNode> {
    override var xParentNodeEntity: Unit? = null
    override var xNodeGrade: Int? = null

    override fun getChildNodeEntityList(): MutableList<CityNode> {
        return city
    }
}

data class CityNode(val name: String, val area: ArrayList<AreaNode>) : NodeEntity<ProvinceNode, AreaNode> {
    override var xParentNodeEntity: ProvinceNode? = null
    override var xNodeGrade: Int? = null

    override fun getChildNodeEntityList(): MutableList<AreaNode> {
        return area
    }
}


//②创建NodeAdapter
val adapter = nodeAdapter()
    .withType<ItemNodeBinding, ProvinceNode> {
        
    }.withType<ItemNodeBinding, CityNode> { 
        
    }.withType<ItemNodeBinding, AreaNode> {
        
    }.toAdapter()
```
