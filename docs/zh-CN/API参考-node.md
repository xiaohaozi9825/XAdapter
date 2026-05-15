# API 参考 · node 模块

依赖：**须同时依赖 `smart` 与 `node`（同一版本 Tag）**。  
包名根前缀：`pw.xiaohaozi.xadapter.node`

---

## 1. 实体

### 1.1 `NodeEntity<Parent, Child>`

| 成员 | 说明 |
|------|------|
| `xParentNodeEntity` / `getParentNodeEntity()` | 父节点；根为 null |
| `xNodeGrade` / `getNodeEntityGrade()` | 层级；未维护时可为 -1 |
| `getChildNodeEntityList()` | 子节点列表 |

### 1.2 `ExpandedNodeEntity`

| 成员 | 说明 |
|------|------|
| `xIsExpanded` / `isExpanded()` | 是否展开子树 |

---

## 2. 工厂（`ext.NodeAdapterExt`）

### 2.1 单布局

```text
inline fun <VB : ViewBinding, D : NodeEntity<*, *>> nodeAdapter(
    itemType: Int = 0,
    crossinline onItemId: OnItemId<VB, D> = { NO_ID },
    crossinline init: (NodeProvider<VB, D, VB, D>.() -> Unit) = {},
    crossinline create: OnAdapterInitHolder<VB, D> = {},
    crossinline bind: OnAdapterBindHolder<VB, D>,
): NodeAdapter<VB, D>
```

- **`bind`**：`NodeAdapter<VB, D>.(OnBindParams<VB, D>) -> Unit`（与 smart 相同，使用 **`OnBindParams`**）。

### 2.2 多布局入口

```text
fun nodeAdapter(
    onItemId: OnItemId<ViewBinding, NodeEntity<*, *>>? = null,
    custom: OnCustomType? = null,
): NodeAdapter<ViewBinding, NodeEntity<*, *>>
```

返回后使用 **`NodeAdapter.withType`** 注册子类型，再在子 **`NodeProvider`** 上 **`toAdapter()`**。

---

## 3. `NodeAdapter`

### 3.1 数据源与刷新

| 成员 / 方法 | 说明 |
|-------------|------|
| `source` | 树形根数据；`refresh(list)` 会赋值并扁平化 |
| `refresh(list)` | 更新 `source` 并 `refresh()` |
| `refresh()` | 按当前 `source` 重新扁平化并 `notifyDataSetChanged` |

### 3.2 节点增删改

| 方法 | 说明 |
|------|------|
| `addNode` / `addNode(nodes, index?)` | 根节点 |
| `addChildNode` / `addChildNode(nodes, …)` | 指定父下子节点 |
| `removeNode` / `removeNodeAt` / `removeNode(start,count)` / `removeNodeList` | 删根 |
| `removeChildNode*` / `removeNodePosition(adapterPosition)` | 删子或按 Adapter 位置删 |
| `updateNode` / `updateChildNode` | 就地替换引用并 `notifyItemChanged` |
| `replaceNode` | 删旧加新（子树整体更新） |
| `findAdapterPosition(node)` | 理论扁平化位置 |

### 3.3 展开折叠

| 方法 | 说明 |
|------|------|
| `expand()` / `collapse()` | 全局展开/收起（`ExpandedNodeEntity`） |
| `expand(position, …)` / `collapse(position, …)` | 按 Adapter 位置展开/收起子树 |

### 3.4 `withType`

与 smart 类似，在 **`NodeAdapter`** 上注册 **`NodeProvider`** 子类型；**必须**写清 `reified vb`、`reified d` 泛型。

---

## 4. `NodeProvider`

| 方法 | 说明 |
|------|------|
| `withType` | 子布局再分类型 |
| `toAdapter()` | 回到 **`NodeAdapter`** |

事件能力来自 smart 的 **`EventImpl`** 委托（`setOnClickListener` 等同 smart）。

---

## 5. 扩展（`ext.NodeAdapterExt`）

| 符号 | 说明 |
|------|------|
| `swipeMenu` | 同 smart，根布局需 `SwipeItemLayout` |
| `swipeDelete` | `NodeAdapter` / `NodeProvider` 版本；node 包内 **`SwipeDelete`** 默认删除行为对接 **`removeNodePosition`** |

---

## 6. 与 smart 的关系

- **`NodeAdapter`** 继承 **`XAdapter`**，头脚空页、**`bindLifecycle`**、**`customItemType`** 等与 smart 一致。  
- 数据操作以 **树 `source` + 扁平 `getDataList()`** 两套为准：改树后需 **`refresh()`** 或走节点 API 内已带的局部 `notify`。

返回 [文档首页](./README.md) · [完整教程](./完整教程.md) · [smart API](./API参考-smart.md)
