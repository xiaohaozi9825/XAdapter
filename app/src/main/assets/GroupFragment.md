## 分组布局
> 将指定类型填充整行，主要正对网格布局和瀑布流布局使用


#### 核心方法

```kotlin
private val adapter = createAdapter()
    .withType<ItemHomeTitleBinding, String>(isFixed = true) {
        it.holder.binding.tvTitle.text = it.data
    }
    .withType<ItemImageAutoHeightBinding, Int> {
        it.holder.binding.ivImage.setImageResource(it.data)
    }
    .toAdapter()
```

##### 参数说明
- isFixed ：是否填充整行，实现分组效果



