# 分组布局
> 将指定类型填充整行，主要正对网格布局和瀑布流布局使用
## 用法

### 方法定义
```kotlin
/**
 * @param isFixed 是否填充整行，仅线性布局、网格布局、瀑布流布局有效。
 */
inline fun <reified pvb : VB, reified pd : D> withType(
    isFixed: Boolean? = null,
    itemType: Int? = null,
    init: (SmartProvider<VB, D, pvb, pd>.() -> Unit) = {},
    crossinline created: OnProviderCreatedHolder<VB, D, pvb, pd> = {},
    crossinline bind: OnProviderBindHolder<VB, D, pvb, pd>,
)
```

### 基础用法
```kotlin
val adapter = createAdapter()
    //将形参isFixed置为true，则该类型填充整行，实现分组效果
    .withType<ItemHomeTitleBinding, String>(isFixed = true) {
        
    }
    .withType<ItemImageAutoHeightBinding, Int> {
      
    }
    .toAdapter()
```


## 示例代码

item_home_title.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/tv_title"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="8dp"
    android:layout_marginHorizontal="4dp"
    android:textColor="@color/black"
    android:textSize="16sp"
    android:textStyle="bold" />

```
item_image_auto_height.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="4dp"
    app:cardCornerRadius="6dp"
    app:cardElevation="0dp">

    <ImageView
        android:id="@+id/iv_image"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:adjustViewBounds="true"
        android:src="@mipmap/snow1"
        android:scaleType="fitCenter" />

</androidx.cardview.widget.CardView>

```


GroupFragment.kt

```kotlin
class GroupFragment : VBFragment<FragmentRecyclerBinding>() {
    val TAG = "GroupFragment"
    private val adapter = createAdapter()
        .withType<ItemHomeTitleBinding, String>(isFixed = true) {
            it.holder.binding.tvTitle.text = it.data
        }
        .withType<ItemImageAutoHeightBinding, Int> {
            it.holder.binding.ivImage.setImageResource(it.data)
        }
        .toAdapter()


    override fun FragmentRecyclerBinding.initView() {
        binding.recycleView.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        binding.recycleView.adapter = adapter
        adapter.refresh(list)
    }


    private val list = arrayListOf(
        "2024-03-24",
        R.mipmap.snow1,
        R.mipmap.t3,
        R.mipmap.y5,
        R.mipmap.snow2,
        R.mipmap.snow3,
        "2024-05-18",
        R.mipmap.t1,
        R.mipmap.t2,
        R.mipmap.t4,
        R.mipmap.t5,
        R.mipmap.t6,
        R.mipmap.t7,
        R.mipmap.t8,
        R.mipmap.t9,
        "2024-10-04",
        R.mipmap.t10,
        R.mipmap.y1,
        R.mipmap.y2,
        R.mipmap.y3,
        R.mipmap.y4,
        R.mipmap.y6,
        R.mipmap.y7,
        R.mipmap.y8,
        R.mipmap.y9,
        R.mipmap.y10,
        )
}
```



