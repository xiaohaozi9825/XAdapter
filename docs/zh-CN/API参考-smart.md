# API 参考 · smart 模块

包名根前缀：`pw.xiaohaozi.xadapter.smart`  
主入口扩展：`pw.xiaohaozi.xadapter.smart.ext`（`createAdapter`、`createTypeAdapter`、`swipeDelete` 等）

以下列举 **集成方最常用** 的 API；未列出的内部方法请以源码与 KDoc 为准。

---

## 1. 工厂与类型别名（`ext.SmartAdapterExt`）

### 1.1 `createAdapter`

```text
inline fun <reified VB : ViewBinding, reified D> createAdapter(
    itemType: Int = 0,
    crossinline onItemId: OnItemId<VB, D> = { NO_ID },
    crossinline init: (SmartProvider<VB, D, VB, D>.() -> Unit) = {},
    crossinline created: OnAdapterInitHolder<VB, D> = {},
    crossinline bind: OnAdapterBindHolder<VB, D>,
): SmartAdapter<VB, D>
```

- **`bind`**：`SmartAdapter<VB, D>.(params: OnBindParams<VB, D>) -> Unit`。

### 1.2 `createTypeAdapter`

```text
inline fun <reified VB : ViewBinding, reified D> createTypeAdapter(
    noinline onItemId: OnItemId<VB, D>? = null,
    noinline custom: OnCustomType<VB, D>? = null,
): SmartAdapter<VB, D>
```

用于多布局入口（各 item **数据类型有公共父类型 `D`** 时）；再通过 `withType` 注册各子类型。

若多种 item **数据类型互不继承**（任意类混排），请使用 **`createAdapter()`** 无泛型版本（`SmartAdapter<ViewBinding, Any?>`），见 [完整教程 §4](./完整教程.md#4-smart多布局列表)。

### 1.3 `createAdapter`（多类型宽松版）

```text
fun createAdapter(
    onItemId: OnItemId<ViewBinding, Any?>? = null,
    custom: OnCustomType<ViewBinding, Any?>? = null,
): SmartAdapter<ViewBinding, Any?>
```

等价于 `createTypeAdapter<ViewBinding, Any?>(...)`。

### 1.4 `LifecycleOwner.createLifecycleAdapter` / `createLifecycleTypeAdapter`

在 `createAdapter` / `createTypeAdapter` 创建后自动 **`adapter.bindLifecycle(this)`**。

### 1.5 扩展：点击 / 侧滑 / 拖拽 / 单选

| 符号 | 接收者 | 说明 |
|------|--------|------|
| `onClick` / `onLongClick` | `SmartAdapter` / `SmartProvider` | 简化事件 DSL |
| `swipeMenu` | `SmartAdapter` | 侧滑菜单 |
| `swipeDelete` | `SmartAdapter` / `SmartProvider` | 侧滑删除 |
| `dragSort` | `SmartAdapter` / `SmartProvider` | 拖拽排序 |
| `singleSelect`（多重重载） | `SmartAdapter` | 单选快捷配置 |

---

## 2. `SmartDataProxy`（数据操作）

`SmartAdapter` **委托**实现；在 `SmartAdapter` 上直接调用下列方法即可。

### 2.1 非 Differ 模式

| 方法 | 说明 |
|------|------|
| `setList(list)` | 替换整个 `MutableList` 引用并刷新 |
| `refresh(list)` | 清空再 `addAll`，整表 `notifyDataSetChanged` |
| `add` / `add(index, …)` | 追加或插入 |
| `removeAt` / `remove(start, count)` / `remove(data)` / `remove(list)` / `remove()` | 删除 |
| `updateAt` / `update` / `update(list)` | 更新，`payload` 可选 |
| `swap(from, to)` | 交换两项 |

### 2.2 Differ 模式

| 方法 | 说明 |
|------|------|
| `setDiffer(diffCallback, listener)` | 使用 `DiffUtil.ItemCallback` 初始化 `AsyncListDiffer` |
| `setDiffer(config, listener)` | 使用 `AsyncDifferConfig` |
| `submitList(list)` / `submitList(list, commitCallback)` | 提交新列表 |

**约束**：Differ 开启后，**勿**使用 §2.1 中标注为仅非 Differ 的增删改（与接口注释一致）。

---

## 3. `EventProxy`（事件）

| 方法 | 说明 |
|------|------|
| `setOnClickListener(id, listener)` | `id == null` 表示 item 根视图 |
| `setOnLongClickListener` | 返回 `Boolean` |
| `setOnCheckedChangeListener` | `CompoundButton` |
| `setOnTextChange` | `TextView` 系 |

`listener` 的 receiver 为 **Employer**（`SmartAdapter` 或 `SmartProvider`）。

---

## 4. `SelectedProxy`（选择）

| 方法 / 属性 | 说明 |
|-------------|------|
| `setOnItemSelectListener`（多重重载） | 指定触发 view id、`payload`、允许的 `itemType` 或 `Class<*>` |
| `setOnSelectAllListener` | 全选状态变化 |
| `setMaxSelectCount` / `isAutoCancel` / `isAllowCancel` / `isUpdateIndexChangeItem` | 行为配置 |
| `setSelectionSame` / `selectionSame` | 自定义「同一项」判断 |
| `setSelectAt` / `setSelect` | 程序化改选中 |
| `isSelectAll` / `selectAll` / `deselectAll` | 全选相关 |
| `getSelectedList` | 当前选中数据 |
| `isSelectedAt` / `isSelected` / `getSelectedIndexAt` / `getSelectedIndex` | 查询 |

---

## 5. `SmartAdapter` / `SmartProvider`

### 5.1 `SmartAdapter.withType`

```text
inline fun <reified pvb : VB, reified pd : D> withType(
    isFixed: Boolean? = null,
    itemType: Int? = null,
    init: (SmartProvider<...>.() -> Unit) = {},
    crossinline created: OnProviderCreatedHolder<...> = {},
    crossinline bind: OnProviderBindHolder<...>,
): SmartProvider<...>
```

多布局注册子 Provider。

### 5.2 `SmartProvider`

| 方法 | 说明 |
|------|------|
| `getSmartAdapter()` | 所属 `SmartAdapter` |
| `withType` | 在子 Provider 上再嵌套类型 |
| `toAdapter()` | 回到外层 `SmartAdapter` |

---

## 6. `XAdapter`（列表与特殊布局）

### 6.1 数据与坐标

| 方法 | 说明 |
|------|------|
| `getDataList()` | 当前数据列表（Differ 下为 `currentList`） |
| `getData(position)` | 含头/脚/空/缺省页时可能非 `D` |
| `getDataPosition(adapterPosition)` / `getAdapterPosition(dataPosition)` | 坐标换算 |
| `bindLifecycle(owner)` | 绑定生命周期 |

### 6.2 Provider 与类型

| 方法 | 说明 |
|------|------|
| `addProvider(provider, itemType?)` | 注册 `TypeProvider` |
| `customItemType { data, pos -> }` | 自定义 itemType |
| `plus(provider)` | `addProvider` 运算符形式 |

### 6.3 头 / 脚 / 空 / 缺省页

| 方法 | 说明 |
|------|------|
| `addHeader` / `removeHeader` | 头布局 DSL |
| `addFooter` / `removeFooter` | 脚布局 |
| `setEmpty` | 空状态 |
| `setDefaultPage` / `showDefaultPage` / `hintDefaultPage` | 缺省页 |

### 6.4 其它

| 方法 | 说明 |
|------|------|
| `isDifferMode()` | 是否已 `setDiffer` |
| `notifyAllItemChanged` | 范围 `notifyItemRangeChanged` |
| `addOnViewHolderChanges` / `addOnRecyclerViewChanges` / `addOnViewChanges` | 生命周期类监听 |

---

## 7. `OnBindParams` / `XHolder`

### 7.1 `OnBindParams<VB, D>`

字段：`holder`、`data`、`position`、`payloads`、`scope`；属性：`binding`（同 `holder.binding`）。  
实现 **`CoroutineScope`**，推荐 **`params.launch { }`** 做绑定内异步。

### 7.2 `XHolder`

| 方法 | 说明 |
|------|------|
| `getXPosition()` | 尽量返回有效 adapter 位置 |
| `isRoutineLayout()` | 是否为普通数据行（相对头脚空等） |

---

## 8. 实体与枚举

- **`XMultiItemEntity`**：多类型 item 提供 `getItemViewType()`。  
- **`HEADER` / `FOOTER` / `EMPTY` / `DEFAULT_PAGE`**：特殊布局占位数据类型（见 `entity` 包）。

---

## 9. 相关类型

- **`TypeProvider` / `XProvider`**：`ViewHolder` 创建与绑定链路。  
- **`ObservableList`**：列表变更回调（选中实现等内部使用）。

返回 [文档首页](./README.md) · [完整教程](./完整教程.md) · [node API](./API参考-node.md)
