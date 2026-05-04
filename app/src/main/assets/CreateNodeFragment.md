# Node创建单布局
> 创建一个Node布局
## 用法

### 方法定义
```kotlin
/**
 * 创建单布局Adapter
 * @param itemType
 * @param onItemId
 * @param init
 * @param create
 * @param bind
 * @return NodeAdapter<VB, D>
 */
inline fun <VB : ViewBinding, D : NodeEntity<*, *>> nodeAdapter(
    itemType: Int = 0,
    crossinline onItemId: OnItemId<VB, D> = { NO_ID },
    crossinline init: (NodeProvider<VB, D, VB, D>.() -> Unit) = {},
    crossinline create: OnAdapterInitHolder<VB, D> = {},
    crossinline bind: OnAdapterBindHolder<VB, D>,
): NodeAdapter<VB, D> 
```

### 基础用法
```kotlin
//①数据类必须实现NodeEntity接口，并重写getChildNodeEntityList()方法
data class NodeInfo(val name: String, val child: MutableList<NodeInfo>? = null) : NodeEntity<NodeInfo?, NodeInfo>{
    override var xParentNodeEntity: NodeInfo? = null
    override var xNodeGrade: Int? = null

    override fun getChildNodeEntityList(): MutableList<NodeInfo>? {
        return child
    }
}

//②创建NodeAdapter
val adapter = nodeAdapter<ItemNodeBinding, NodeInfo> { 
    
    }
```
