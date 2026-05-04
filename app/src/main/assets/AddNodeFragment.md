# 添加Node
> 添加根节点或子节点
## 用法

### 方法定义
```kotlin
/**
 * 在根节点中添加一个节点
 * @param node
 * @param index 指定位置(相对数据源)，null 在末尾添加
 */
fun addNode(node: D, index: Int? = null)

/**
 * 在根节点中添加多个节点
 * @param nodes
 * @param index 指定添加位置，不传则在末尾添加
 */
fun <L : Collection<D>> addNode(nodes: L, index: Int? = null)

/**
 * 在parent节点中添加一个子节点node
 * @param parent
 * @param node
 * @param index node在parent中的位置
 *
 */
fun addChildNode(parent: D, node: D, index: Int? = null)

/**
 * 在parent节点中添加多个子节点
 * @param parent
 * @param nodes
 * @param index nodes在parent中的位置
 */
@Suppress("UNCHECKED_CAST")
fun <L : Collection<D>> addChildNode(parent: D, nodes: L, index: Int? = null)
```

