# XAdapter

> Android RecyclerView Adapter 封装库，旨在快速创建和快速使用Adapter，一个方法实现一个功能。

## 接入XAdapter

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

```
dependencies {
    implementation 'com.github.xiaohaozi9825:XAdapter:Tag'
}
```

#### 步骤3.启用ViewBinding或DataBinding

在项目 build.gradle 文件中android节点中添加

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

#### Adapter

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

#### 数据操作

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
- `fun submitList(list: List<D>)`Deffer模式更新数据

#### 事件监听

- setOnClickListener：点击事件监听
- setOnLongClickListener：长按事件监听
- setOnCheckedChangeListener：选中状态监听
- setOnTextChange：文本变化监听

#### 选择操作

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

#### 创建单布局Adapter
调用createAdapter()方法，泛型VB确定布局文件，D确定数据类型，回调方法中完成数据与视图的绑定。
```kotlin
val adapter = createAdapter<ItemVerseBinding, VerseInfo> { (holder, data) ->
    holder.binding.tvContent.text = data.content
    holder.binding.tvAuthor.text = data.author
}
```

#### 创建多布局Adapter
调用createAdapter()方法创建adapter实例；使用withType切换布局类型，返回Provider；调用toAdapter()方法将Provider转换为adapter。
```kotlin
val adapter = createAdapter()
    .withType<ItemVerseBinding, VerseInfo> { (holder, data, position) ->
        holder.binding.tvContent.text = data.content
        holder.binding.tvAuthor.text = data.author
    }
    .withType<ItemImageCardBinding, Int> { (holder, data, position) ->
        holder.binding.image.setImageResource(data)
    }
    .toAdapter()
```

## 相关地址

demo体验：[https://www.pgyer.com/7kPKon2W](https://www.pgyer.com/7kPKon2W)

github：[https://github.com/xiaohaozi9825/XAdapter](https://github.com/xiaohaozi9825/XAdapter)

gitee：[https://gitee.com/xiaohaozi9825/xadapter](https://gitee.com/xiaohaozi9825/xadapter)

简书：[https://www.jianshu.com/p/936be339b378?v=1735022668540](https://www.jianshu.com/p/936be339b378?v=1735022668540)
