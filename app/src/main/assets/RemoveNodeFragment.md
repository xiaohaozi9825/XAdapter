# Node删除
> 删除根节点或子节点
## 用法

### 方法定义
```kotlin
/**
 * 根节点中删除一个节点
 * @param node 被删除的节点
 */
fun removeNode(node: D)

/**
 * 根节点中删除指定位置的节点
 * @param index 被删除的节点
 */
fun removeNodeAt(index: Int)

/**
 * 从start位置开始，删除count个节点
 * @param start 开始位置
 * @param count 删除数量
 */
fun removeNode(start: Int, count: Int)

/**
 * 删除多个节点
 * @param nodes 被删除的节点
 */
fun removeNodeList(nodes: List<D>)

/**
 * 删除子节点
 */
fun removeChildNode(parent: D, node: D)

/**
 * 删除子节点
 */
@Suppress("UNCHECKED_CAST")
fun removeChildNodeAt(parent: D, index: Int)

/**
 * 删除子节点
 */
fun removeChildNode(parent: D, start: Int, count: Int)

/**
 * 删除子节点
 */
fun removeChildNodeList(parent: D, nodes: List<D>)

/**
 * 删除指定位置的节点，该位置是对应adapter中的位置，一般用于删除当前item
 * @param adapterPosition 该节点在adapter中的位置
 */
@Suppress("UNCHECKED_CAST")
fun removeNodePosition(adapterPosition: Int)
```

