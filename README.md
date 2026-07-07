# XAdapter

> Android RecyclerView Adapter 封装库，旨在快速创建和快速使用 Adapter，一个方法实现一个功能。

- **SDK 模块**：`smart`（必选）、`node`（可选，树形/多级列表）  
- **Demo 模块**：`app`（仅演示，**不要**作为依赖引入业务工程）  
- **发版**：通过 [JitPack](https://jitpack.io/#xiaohaozi9825/XAdapter) 分发，适合「只加依赖、不 fork 源码」的集成方式  

**中文文档（接入 / 环境 / 混淆 / 教程 / API）**：[docs/zh-CN/README.md](docs/zh-CN/README.md)  

- **完整教程**：[docs/zh-CN/完整教程.md](docs/zh-CN/完整教程.md)  
- **API 说明**： [smart](docs/zh-CN/API参考-smart.md) · [node](docs/zh-CN/API参考-node.md)  

---

## 接入 XAdapter（精简版）

更完整的步骤、环境版本、混淆说明见 **[docs/zh-CN/接入指南.md](docs/zh-CN/接入指南.md)**；**用法教程**见 **[docs/zh-CN/完整教程.md](docs/zh-CN/完整教程.md)**，**符号级 API** 见 **[docs/zh-CN/API参考-smart.md](docs/zh-CN/API参考-smart.md)** / **[docs/zh-CN/API参考-node.md](docs/zh-CN/API参考-node.md)**。

#### 步骤1. 将 JitPack 添加到您的 build 文件中

在您工程 build.gradle 文件中添加:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

#### 步骤2. 添加依赖

[![](https://jitpack.io/v/xiaohaozi9825/XAdapter.svg)](https://jitpack.io/#xiaohaozi9825/XAdapter)

将 **`<Tag>`** 替换为你在 JitPack 选用的 **Git Tag 或 commit**（须与页面已成功构建的版本一致）。页面 **Gradle**  tab 可复制官方生成的坐标。

```
dependencies {
    // 核心 SDK（必选）
    implementation 'com.github.xiaohaozi9825.XAdapter:smart:<Tag>'
    // 树形 / 多级列表（可选，与 smart 同 Tag）
    implementation 'com.github.xiaohaozi9825.XAdapter:node:<Tag>'
}
```

#### 步骤3.启用ViewBinding或DataBinding

在项目 build.gradle 文件中android节点中添加，dataBinding与viewBinding可启用其中一个，也可以两个都启用。

```
buildFeatures {
    dataBinding = true
    viewBinding = true
}
```

## 优势特点

充分利用kotlin语法特性，结合ViewBinding或DataBinding，基本可以一个方法实现一个功能。用最简单的方式，实现复杂的功能，极大提高开发效率。

- 单布局一个方法即可创建，无需使用继承。
- 多布局自动计算itemType，无需手动判断。
- 封装了对数据增、删、改、查、交换、刷新等操作方法。
- 封装了View 点击事件、长按事件、选中状态、文本变化等监听方法。
- 封装了Item选择操作，可设置选择数量、全选、全部选等方法。
- 集成了Differ刷新算法，可实现ListAdapter类似效果。
- 封装了特殊布局，如头布局、脚布局、空布局、缺省页、分组等。
- 封装了侧滑删除、拖拽排序、侧滑菜单等常用操作。
- 使用泛型约束，回调方法中数据类型自动转换。
- 集成了协程，ViewHolder回收时自动取消协程。
- 集成了lifecycle生命周期管理。
- 方法返回adapter，方便链式操作。
- 回调方法this指向adapter。

## 功能锦集

V1.0 smart

- 快速创建：单布局创建、多布局创建
- 事件监听：点击事件、长按事件、选中状态变化、文本内容变化
- 特殊布局：头布局、脚布局、空布局、缺省页布局、分组布局
- 常用操作：侧滑删除、拖拽排序、侧滑菜单
- 选择操作：单选、多选、全选、全不选
- 数据操作：添加数据、删除数据、修改数据、获取列表数据、获取已选列表

V2.0 node

- 快速创建：单布局node创建、多布局node创建
- 展开折叠：展开或收起子节点
- 数据操作：添加节点、删除节点、修改节点、替换节点

#### Adapter

V1.0 smart

- createAdapter：创建Adapter
- withType：类型转换
- bindLifecycle：绑定生命周期
- provider.isFixedViewType：是否填充整行（分组布局）
- addHeader：添加头布局
- addFooter：添加脚布局
- setEmpty：设置空布局
- setDefaultPage：设置缺省页
- swipeDelete：设置侧滑删除
- dragSort：设置拖拽排序
- swipeMenu：设置侧滑菜单

V2.0 node

- nodeAdapter：创建NodeAdapter
- withType：切换类型
- expand：展开子node
- collapse：收起子node

#### 数据操作

V1.0 smart

- `fun <L : MutableList<D>> setList(list: L)`设置数据
- `fun <L : Collection<D>> refresh(list: L)`刷新数据
- `fun <L : Collection<D>> add(list: L)`添加数据
- `fun add(data: D)`添加数据
- `fun add(index: Int, data: D)`添加数据
- `fun <L : Collection<D>> add(index: Int, list: L)`添加数据
- `fun removeAt(index: Int)`删除数据
- `fun remove(start: Int, count: Int)`删除数据
- `fun remove(data: D)`删除数据
- `fun <L : Collection<D>> remove(list: L)`删除数据
- `fun remove()`删除数据
- `fun updateAt(index: Int, data: D, payload: Any? = null)`更新数据
- `fun updateAt(index: Int, payload: Any? = null)`更新数据
- `fun update(data: D, payload: Any? = null)`更新数据
- `fun <L : Collection<D>> update(list: L, payload: Any? = null)`更新数据
- `fun swap(fromPosition: Int, toPosition: Int)`交换数据
- `fun setDiffer(): Employer`设置Differ模式
- `fun submitList(list: List<D>)`Differ模式更新数据

V2.0 node
- `addNode(node: D, index: Int? = null)` 添加一个根节点
- `addNode(nodes: L, index: Int? = null)` 添加多个根节点
- `addChildNode(parent: D, node: D, index: Int? = null)` 添加一个子节点
- `addChildNode(parent: D, nodes: L, index: Int? = null)` 添加多个子节点
- `removeNode(node: D)` 删除一个根节点
- `removeNodeAt(index: Int)` 删除指定索引的根节点
- `removeNode(start: Int, count: Int)` 从start位置开始删除count个根节点
- `removeNodeList(nodes: List<D>)` 删除多个根节点
- `removeChildNode(parent: D, node: D)` 删除一个子节点
- `removeChildNodeAt(parent: D, index: Int)` 删除指定位置一个子节点
- `removeChildNode(parent: D, start: Int, count: Int)` 从start位置开始删除count个子节点
- `removeChildNodeList(parent: D, nodes: List<D>)` 删除多个子节点
- `removeNodePosition(adapterPosition: Int)` 删除position处的一个节点（可以是根节点，也可以是子节点）
- `updateNode(node: D, payload: Any? = null)` 更新一个根节点
- `updateNode(oldNode: D, newNode: D, payload: Any? = null)` 更新一个根节点
- `updateChildNode(parent: D, oldNode: D, newNode: D, payload: Any? = null)` 更新一个子节点
- `replaceNode(oldNode: D, newNode: D) ` 替换一个节点

#### 事件监听

V1.0 smart

- setOnClickListener：点击事件监听
- setOnLongClickListener：长按事件监听
- setOnCheckedChangeListener：选中状态监听
- setOnTextChange：文本变化监听

#### 选择操作

V1.0 smart

- setOnItemSelectListener：设置选择事件监听
- setOnSelectAllListener：设置全选监听
- setMaxSelectCount：设置最大可选数
- isAutoCancel：超出最大可选数时，是否自动取消第一个选中对象
- isAllowCancel：是否允许点击后取消
- setSelectAt：设置指定位置item选中状态
- setSelect：设置指定数据item选中状态
- isSelectAll：是否全选
- selectAll：全选
- deselectAll：全不选
- getSelectedList：获取已选列表
- isSelectedAt：指定位置item是否选择
- isSelected：指定数据是否选择
- getSelectedIndexAt：获取指定位置item选择编号
- getSelectedIndex：获取指定数据item选择编号

## 示例代码

绑定回调统一为 **`OnBindParams`**（属性：`binding`、`data`、`position`、`payloads`；可作为 `CoroutineScope` 使用）。详见 [完整教程](docs/zh-CN/完整教程.md)。

#### 创建单布局 Adapter

```kotlin
val adapter = createAdapter<ItemVerseBinding, VerseInfo> { p ->
    p.binding.tvContent.text = p.data.content
    p.binding.tvAuthor.text = p.data.author
}
```

#### 创建多布局 Adapter

```kotlin
val adapter = createAdapter()
    .withType<ItemVerseBinding, VerseInfo> { p ->
        p.binding.tvContent.text = p.data.content
    }
    .withType<ItemImageCardBinding, ImageItem> { p ->
        p.binding.image.setImageResource(p.data.resId)
    }
    .toAdapter()
```

若各类型数据有统一父类型（如 `sealed class Row`），可使用 `createTypeAdapter<ViewBinding, Row>()`，再为各子类写 `withType`。


