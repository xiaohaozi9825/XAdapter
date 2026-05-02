## 使用Differ更新数据
> 用法与ListAdapter类似，更新数据时用submitList方法


#### 参考代码

```kotlin
val itemCallback: ItemCallback<VerseInfo> = object : ItemCallback<VerseInfo>() {
    override fun areItemsTheSame(oldItem: VerseInfo, newItem: VerseInfo): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: VerseInfo, newItem: VerseInfo): Boolean {
        return oldItem == newItem
    }
}
//声明adapter使用differ
adapter.setDiffer(itemCallback)

//更新数据
adapter.submitList(ArrayList(dataList))
```

##### 参数说明
- diffCallback：数据匹配规则
- listener：数据变更回调



