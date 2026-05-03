# 特殊布局

> 特殊布局主要有头布局、脚布局、空布局、缺省页，以及分组布局。分组布局不在该示例中演示。

## 用法

### 方法定义

```kotlin
/**
 * 添加头布局
 * 改方法可动态设置，设置后直接展示。
 *
 * @param tag 备用字段，可用于标记，或数据存储与传递
 * @param init 初始化时回调，可在此设置事件监听操作
 * @param create 创建ViewHolder后调用，可用于初始化item
 * @param bind 绑定视图时调用
 */
inline fun <reified vb : ViewBinding> addHeader(
    tag: String = "",
    noinline init: (XProvider<vb, HEADER>.() -> Unit)? = null,
    noinline create: (XProvider<vb, HEADER>.(holder: XHolder<vb>) -> Unit)? = null,
    noinline bind: (XProvider<vb, HEADER>.(holder: XHolder<vb>, data: HEADER) -> Unit)? = null,
)

/**
 * 删除指定头布局
 */
inline fun <reified T : ViewBinding> removeHeader(): R {
    removeHeaderProvider<T>()
    @Suppress("UNCHECKED_CAST")
    return this as R
}

/**
 * 添加脚布局
 * 改方法可动态设置，设置后直接展示。
 *
 * @param tag 备用字段，可用于标记，或数据存储与传递
 * @param init 初始化时回调，可在此设置事件监听操作
 * @param create 创建ViewHolder后调用，可用于初始化item
 * @param bind 绑定视图时调用
 */
inline fun <reified vb : ViewBinding> addFooter(
    tag: String = "",
    noinline init: (XProvider<vb, FOOTER>.() -> Unit)? = null,
    noinline create: (XProvider<vb, FOOTER>.(holder: XHolder<vb>) -> Unit)? = null,
    noinline bind: (XProvider<vb, FOOTER>.(holder: XHolder<vb>, data: FOOTER) -> Unit)? = null,
)

/**
 * 删除指定脚布局
 */
inline fun <reified T : ViewBinding> removeFooter(): R {
    removeFooterProvider<T>()
    @Suppress("UNCHECKED_CAST")
    return this as R
}

/**
 * 设置空布局
 * 改方法需在初始化adapter时设置，设置后并不直接显示。
 * 当adapter无数据时自动显示，有数据时自动隐藏。
 *
 * @param init 初始化时回调，可在此设置事件监听操作
 * @param create 创建ViewHolder后调用，可用于初始化item
 * @param bind 绑定视图时调用
 */
inline fun <reified vb : ViewBinding> setEmpty(
    noinline init: (XProvider<vb, EMPTY>.() -> Unit)? = null,
    noinline create: (XProvider<vb, EMPTY>.(holder: XHolder<vb>) -> Unit)? = null,
    noinline bind: (XProvider<vb, EMPTY>.(holder: XHolder<vb>) -> Unit)? = null,
)

/**
 * 设置缺省页
 * 改方法需在初始化adapter时设置，设置后并不直接显示。
 * 显示与隐藏需调用：
 * @see showDefaultPage
 * @see hintDefaultPage
 *
 * @param tag 备用字段，可用于标记，或数据存储与传递
 * @param init 初始化时回调，可在此设置事件监听操作
 * @param create 创建ViewHolder后调用，可用于初始化item
 * @param bind 绑定视图时调用
 */
inline fun <reified vb : ViewBinding> setDefaultPage(
    tag: Any = "",
    noinline init: (XProvider<vb, DEFAULT_PAGE>.() -> Unit)? = null,
    noinline create: (XProvider<vb, DEFAULT_PAGE>.(holder: XHolder<vb>) -> Unit)? = null,
    noinline bind: (XProvider<vb, DEFAULT_PAGE>.(holder: XHolder<vb>, data: DEFAULT_PAGE) -> Unit)? = null,
)
```

### 基础用法

```kotlin
//设置空布局，无数据时自动展示，初始化时设置，只能设置一个。
adapter.setEmpty<ItemEmptyBinding>()

//设置加载中缺省页，调用showDefaultPage()/hintDefaultPage()方法控制显示/隐藏，初始化时设置，只能设置一个。
adapter.setDefaultPage<ItemLoadingBinding>()

//添加并展示头布局，可以多个，可通过hasHeader属性控制隐藏/显示，
//也可通过removeHeader<ItemHomeHeaderBinding>()方法删除，可动态设置。
adapter.addHeader<ItemHomeHeaderBinding>()

//添加并展示头布局，可以多个，可通过hasFooter属性控制隐藏/显示，
//也可通过removeFooter<ItemHomeFooterBinding>()方法删除，可动态设置。
adapter.addFooter<ItemHomeFooterBinding>()
```

