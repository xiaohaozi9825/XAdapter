# Node交换数据
> 交换数据，与updateNode不同点在于：updateNode用于数据内部属性值变化，adapter负责刷新当前item，而不会刷新子node；而replaceNode在刷新当前item的同时，也会对子item同步跟新。
## 用法

### 方法定义
```kotlin
/**
 * 交换数据
 * 该方法会对所有子节点更新
 *
 */
@Suppress("UNCHECKED_CAST")
fun replaceNode(oldNode: D, newNode: D) 
```
