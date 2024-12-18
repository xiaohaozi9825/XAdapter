## 与ConcatAdapter结合使用
> 注意：与ConcatAdapter结合使用时，不支持特殊布局，如：头布局、脚布局、空布局、缺省页布局、分组布局等

#### 创建子adapter
创建子adapter与普通adapter一样创建
```java
private val adapterHeader = createAdapter<ItemHomeHeaderBinding, String>(1, onItemId = { _ -> -1 }) { }
private val adapterFooter = createAdapter<ItemHomeFooterBinding, String>(2, onItemId = { _ -> -2 }) { }
private val adapterBody = createAdapter(onItemId = { position: Int -> position.toLong() }) { data, _ ->
    return@createAdapter if (data is VerseInfo) 3 else 4
}
    .withType<ItemVerseBinding, VerseInfo>(itemType = 3) { (holder, data) ->
        holder.binding.tvContent.text = data.content
        holder.binding.tvAuthor.text = data.author
    }
    .withType<ItemImageCardBinding, Int>(itemType = 4) { (holder, data) ->
        holder.binding.image.setImageResource(data)
    }
    .toAdapter()
```

#### 与ConcatAdapter结合

```kotlin
val config = ConcatAdapter.Config.Builder()
//isolateViewTypes：如果使用false，需要明确指定各itemType值,且保证在所有adapt。默认true
config.setIsolateViewTypes(false)


//NO_STABLE_IDS
//ISOLATED_STABLE_IDS
//SHARED_STABLE_IDS
//默认NO_STABLE_IDS，其他模式需要子adapter设置HasStableIds，注意各adapter中getItemId方法返回值
adapterHeader.setHasStableIds(true)
adapterBody.setHasStableIds(true)
adapterFooter.setHasStableIds(true)
config.setStableIdMode(ConcatAdapter.Config.StableIdMode.SHARED_STABLE_IDS)


val build = config.build()
val concatAdapter = ConcatAdapter(
    build,//缺省时使用默认值isolateViewTypes=true,StableIdMode=NO_STABLE_IDS
    //以下可以添加多个子adapter
    adapterHeader,
    adapterBody,
    adapterFooter,
)
```




