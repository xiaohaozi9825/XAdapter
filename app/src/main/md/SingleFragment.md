## 单布局创建Adapter

#### 一、创建Adapter，并绑定数据

一般在初始化时调用

##### 方法1

使用XAdapter扩展函数创建

```kotlin
 /**
 * 方法1
 * 使用XAdapter拓展方法创建
 */
private fun function1(): XAdapter<ItemSingleTypeViewBindingBinding, Verse> {
    //泛型VB 确定布局文件，泛型D确定数据类型，回调函数中绑定数据
    return createAdapter<ItemSingleTypeViewBindingBinding, Verse> { holder, data, position ->
        holder.binding.tvContent.text = data.content
        holder.binding.tvAuthor.text = data.author
    }
}
```

##### 方法2

使用Adapter+Provider的方式创建

```kotlin
 /**
 * 方法2
 * 使用Adapter+Provider的方式创建
 *
 * 比较：
 * 方法1实际上是对方法2的封装，使用更方便；
 * 方法2步骤繁琐，但是暴露的方法较多，而且可以添加多个Provider，灵活度更高
 *
 * 推荐：
 * 如果逻辑较为简单，推荐使用方法1；
 * 如果逻辑复杂，推荐使用方法2.
 */
private fun function2(): XAdapter<ItemSingleTypeViewBindingBinding, Verse> {
    //①创建Adapter
    val xAdapter = XAdapter<ItemSingleTypeViewBindingBinding, Verse>()
    //②创建Provider
    val provider = object : XProvider<ItemSingleTypeViewBindingBinding, Verse>(xAdapter) {
        override fun onCreated(holder: SmartHolder<ItemSingleTypeViewBindingBinding>) {

        }

        override fun onBind(
            holder: SmartHolder<ItemSingleTypeViewBindingBinding>,
            data: Verse,
            position: Int
        ) {
            holder.binding.tvContent.text = data.content
            holder.binding.tvAuthor.text = data.author
        }

    }
    //③将Provider 添加到 Adapter中
    //方式一：使用方法添加，viewType可不填
//        xAdapter.addProvider(provider, 0)
//        return xAdapter
    //方式一二：使用➕链接，viewType为空
    return xAdapter + provider

}
```

##### 结合 dataBinding 绑定数据
一行代码实现Adapter的创建和数据绑定

```kotlin
  /**
 * 结合 dataBinding 绑定数据，可以一行代码实现Adapter的创建和数据绑定
 *
 * 注意和function1() 中的布局文件不是同一个，
 * function3()的 ItemSingleTypeDataBindingBinding 是使用了dataBinding 的，
 * 而function1()的 ItemSingleTypeViewBindingBinding 没有使用dataBinding的。
 *
 * 此处代码简化前如下,如果单独写，需要注意指定泛型：
 *
 * return createAdapter<ItemSingleTypeDataBindingBinding, Verse> { holder, data, position ->
 *     holder.binding.data = data
 * }
 *
 */
private fun function3(): XAdapter<ItemSingleTypeDataBindingBinding, Verse> {
    //一行代码实现Adapter的创建和数据绑定
    return createAdapter { holder, data, _ -> holder.binding.data = data }
}
```

#### 二、为RecyclerView设置布局管理器和Adapter

一般在初始化时调用

```kotlin
binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
binding.recycleView.adapter = adapter
```

#### 三、给Adapter填充数据

一般在请求数据回调中调用

```kotlin
adapter.reset(list)
```



