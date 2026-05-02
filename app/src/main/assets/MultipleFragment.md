## 多布局创建Adapter

#### 一、创建Adapter，并绑定数据

##### 方法1 使用XAdapter扩展函数创建

1、使用createAdapter()方法创建Adapter；
2、使用withType()方法创建provider；
3、将provider转回adapter。

```kotlin
private fun function1(): XAdapter<ViewBinding, Any?> {
    return createAdapter()
        .withType<ItemVerseBinding, VerseInfo> { holder, data, position ->
            holder.binding.tvContent.text = data.content
            holder.binding.tvAuthor.text = data.author
        }
        .withType<ItemImageCardBinding, Int> { holder, data, position ->
            holder.binding.image.setImageResource(data)
        }
        .toAdapter()
}
```

##### 方法2

使用Adapter+Provider的方式创建

```kotlin

private fun function2(): XAdapter<ViewBinding, Any?> {
    //①创建Adapter
    val xAdapter = XAdapter<ViewBinding, Any?>()
    //②创建Provider
    val provider1 = object : XProvider<ItemImageCardBinding, Int>(xAdapter) {
        override fun onCreated(holder: SmartHolder<ItemImageCardBinding>) {

        }

        override fun onBind(
            holder: SmartHolder<ItemImageCardBinding>,
            data: Int,
            position: Int
        ) {
            holder.binding.image.setImageResource(data)
        }


    }
    val provider2 = object : XProvider<ItemVerseBinding, VerseInfo?>(xAdapter) {
        override fun onCreated(holder: SmartHolder<ItemVerseBinding>) {

        }

        override fun onBind(
            holder: SmartHolder<ItemVerseBinding>,
            data: VerseInfo?,
            position: Int
        ) {
            holder.binding.tvContent.text = data?.content
            holder.binding.tvAuthor.text = data?.author
        }

    }
    //③将Provider 添加到 Adapter中
    //方式一：使用方法添加，itemType可不填

    xAdapter.addProvider(provider2)
    xAdapter.addProvider(provider1)
//
    return xAdapter
    //方式一二：使用➕链接，itemType为空
//        return xAdapter + provider2 + provider1

}
```

#### 二、数据关联itemType

##### 方案 1、自动关联 itemType

按数据类型，自动生成itemType，取值为不重复的负整数

```kotlin
createAdapter()
    .withType<ItemVerseBinding, VerseInfo> { holder, data, position ->
        holder.binding.tvContent.text = data.content
        holder.binding.tvAuthor.text = data.author
    }
    .withType<ItemImageCardBinding, Int> { holder, data, position ->
        holder.binding.image.setImageResource(data)
    }
    .toAdapter()
```

##### 方案 2、实现MultiItemEntity接口

数据类需要实现MultiItemEntity接口，并在getItemViewType()方法中返回itemType值。
数据类中的itemType必须与XAdapter.addProvider()方法中itemType一致

```kotlin

data class MultipleVerseInfo(val verseInfo: VerseInfo) : MultiItemEntity {
    override fun getItemViewType(): Int {
        return 5
    }
}

data class MultipleInt(val res: Int) : MultiItemEntity {
    override fun getItemViewType(): Int {
        return 8
    }
}

createAdapter()
    .withType<ItemVerseBinding, MultipleVerseInfo>(itemType = 5) { holder, data, position ->
        holder.binding.tvContent.text = data.verseInfo.content
        holder.binding.tvAuthor.text = data.verseInfo.author
    }
    .withType<ItemImageCardBinding, MultipleInt>(itemType = 8) { holder, data, position ->
        holder.binding.image.setImageResource(data.res)
    }
    .toAdapter()
```

##### 方案 3、动态关联 itemType

使用回调方法SmartAdapter中customItemType()方法或扩展函数中createAdapter(custom)方法。

```kotlin
 createAdapter{ data, position ->
        if (data is Int) return 9
        else null
    }.withType<ItemVerseBinding, VerseInfo> { holder, data, position ->
        holder.binding.tvContent.text = data.content
        holder.binding.tvAuthor.text = data.author
    }.withType<ItemImageCardBinding, Int>(itemType = 9) { holder, data, position ->
        holder.binding.image.setImageResource(data)
    }.toAdapter()
```

##### 对比

- 方案1方便简单，但是多个provider中数据类型必须不一样
- 方案2数据类必须实现MultiItemEntity接口，多个provider中数据类型可以一样
- 方案3数据类不需要实现MultiItemEntity接口，多个provider中数据类型可以一样，但是计算itemType比较繁琐
- 三种方案可以同时使用，优先级：动态关联 > MultiItemEntity > 自动关联

#### 空数据
数据可以允许为空，但是只能有1个provider数据可空。
- 优先允许itemType == 0 的 provider；
- itemType时，默认允许最小的itemType对应的provider数据可空；
- 如果项目使用了kotlin-reflect库，优先使用provider泛型中数据类型为可空的provider
