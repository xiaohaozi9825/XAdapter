# Node编辑
> 修改节点或子节点
## 用法

### 方法定义
```kotlin
/**
 * 更新数据
 * 如果只是node属性变更，可以调用该方法同步刷新UI
 */
fun updateNode(node: D, payload: Any? = null)

/**
 * 更新数据
 * 可以是任意节点数据
 * 该方法只更新当前数据，不会同步刷新子节点数据,如果需要更新子节点列表，请使用replaceNode()方法。
 * @param oldNode 旧数据
 * @param newNode 新数据
 */
@Suppress("UNCHECKED_CAST")
fun updateNode(oldNode: D, newNode: D, payload: Any? = null)


/**
 * 更新子节点数据
 *
 * 该方法只更新当前数据，不会同步刷新子节点数据，如果需要更新子节点列表，请使用replaceNode()方法。
 * @param oldNode 旧数据
 * @param newNode 新数据
 */
@Suppress("UNCHECKED_CAST")
fun updateChildNode(parent: D, oldNode: D, newNode: D, payload: Any? = null)


```

### 基础用法
```kotlin

```
