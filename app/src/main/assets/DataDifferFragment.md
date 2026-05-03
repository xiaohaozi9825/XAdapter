# Differ操作数据
> 增量修改数据
## 用法

### 方法定义
```kotlin
/**
 * 使用Differ算法迭代数据
 */
fun setDiffer(
    diffCallback: DiffUtil.ItemCallback<D>,
    listener: AsyncListDiffer.ListListener<D> = AsyncListDiffer.ListListener<D> { _, _ -> }
): Employer

/**
 * 使用Differ算法迭代数据
 */
fun setDiffer(
    config: AsyncDifferConfig<D>,
    listener: AsyncListDiffer.ListListener<D> = AsyncListDiffer.ListListener<D> { _, _ -> }
): Employer

/**
 * 在Differ模式下更新数据
 */
fun submitList(list: List<D>)

/**
 * 在Differ模式下更新数据
 */
fun submitList(list: List<D>, commitCallback: Runnable)
```

### 基础用法
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

//初始化时设置 Differ 模式
adapter.setDiffer(itemCallback)

//更新数据时使用
adapter.submitList(ArrayList(dataList))
```

