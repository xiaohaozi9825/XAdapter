# Item选择操作
> 为item触发选择操作，可快速实现单选、多选、全选等操作。
## 用法

### 方法定义
```kotlin
/**
 * 设置选中事件监听
 * @param id 触发选中事件的 view，默认为item
 * @param payload
 * @param permittedTypes 参与选择的类型，与itemType一致
 * @param listener 选中事件回调监听
 */
fun setOnItemSelectListener(
    id: Int? = null,
    payload: Any? = null,
    listener: OnItemSelectListener<Employer, D>
): Employer
```

### 基础用法
```kotlin
val adapter = createAdapter<ItemImageSelectedBinding, Int> { 
        //实现绑定操作
    }.setOnItemSelectListener { data, position, index, fromUser ->
        //设置选择事件，并监听选择操作
    }.setOnSelectAllListener { selectedCache, isSelectedAll ->
        //监听全选状态
    }
    .setMaxSelectCount(9)//设置最大可选数量
    .isAutoCancel(false)//设置是否允许自动取消选中状态
    .isAllowCancel(true)//设置是否允许用户点击取消选中，非用户点击不受影响

//拓展方法singleSelect()可直接实现单选操作
adapter.singleSelect()
```

### 进阶用法
```kotlin
//多布局时，setOnSelectAllListener()默认所有item都设置选择操作，
//如果只有部分itemType需要选择操作，可以使用permittedTypes参数指定。
val adapter = createAdapter()
    .setOnItemSelectListener(
        payload = "select",
        //permittedTypes = arrayOf(java.lang.Integer::class.java)//指定为布局2实现选择操作
        permittedTypes = arrayOf(Int.MIN_VALUE + 1)//指定为布局2实现选择操作
    ) { data, position, index, fromUser ->
        //监听选中操作变化
    }.setOnSelectAllListener { selectedCache, isSelectedAll ->
        //监听全选操作
    }.withType<ItemCameraBinding, Any?> {
        //实现布局1数据绑定
    }.setOnClickListener { holder, data, position, view ->
        //实现布局1点击事件
    }.withType<ItemImageSelectedBinding, Int> { (holder, data, position, payloads) ->
       //实现布局2选择操作
    }.toAdapter()
```

## 示例代码

fragment_selected.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/tv_selected_count"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="8dp"
        android:text="已选0张"
        android:textColor="@color/theme"
        android:textSize="14sp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <LinearLayout
        android:id="@+id/ll_selected_all"
        android:layout_width="wrap_content"
        android:layout_height="0dp"
        android:gravity="center"
        android:orientation="horizontal"
        android:paddingHorizontal="8dp"
        app:layout_constraintBottom_toBottomOf="@id/tv_selected_count"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="@id/tv_selected_count">

        <ImageView
            android:id="@+id/iv_selected_all"
            android:layout_width="16dp"
            android:layout_height="16dp"
            android:background="@drawable/ic_selected"
            android:contentDescription="选择" />

        <TextView
            android:id="@+id/tv_selected_all"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="6dp"
            android:layout_marginEnd="8dp"
            android:text="全选"
            android:textColor="@color/theme"
            android:textSize="14sp" />
    </LinearLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_list"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:padding="6dp"
        app:layoutManager="androidx.recyclerview.widget.GridLayoutManager"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tv_selected_count"
        app:spanCount="3"
        tools:listitem="@layout/item_image_selected" />
</androidx.constraintlayout.widget.ConstraintLayout>
```
item_camera.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="4dp"
    app:cardCornerRadius="6dp"
    app:cardElevation="0dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <ImageView
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:background="#e6e6e6"
            android:scaleType="center"
            android:src="@drawable/ic_camera"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintDimensionRatio="1"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</androidx.cardview.widget.CardView>
```
item_image_selected.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="4dp"
    app:cardCornerRadius="6dp"
    app:cardElevation="0dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <ImageView
            android:id="@+id/iv_image"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:scaleType="center"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintDimensionRatio="1"
            app:layout_constraintTop_toTopOf="parent" />

        <View
            android:id="@+id/view_click"
            android:layout_width="48dp"
            android:layout_height="48dp"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="@id/iv_image" />

        <TextView
            android:id="@+id/tv_selected_index"
            android:layout_width="20dp"
            android:layout_height="20dp"
            android:layout_margin="6dp"
            android:background="@drawable/bg_not_selected"
            android:gravity="center"
            android:text="10"
            android:textColor="@color/white"
            android:textSize="12sp"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="@id/iv_image" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</androidx.cardview.widget.CardView>
```

SelectedFragment.kt

```kotlin
class SelectFragment : VBFragment<FragmentSelectedBinding>() {
    val TAG = "ImageSelectFragment"
    //①创建adapter
    val adapter = createAdapter()
        .setOnItemSelectListener(
            payload = "select",
            //permittedTypes = arrayOf(java.lang.Integer::class.java)
            permittedTypes = arrayOf(Int.MIN_VALUE + 1)
        ) { data, position, index, fromUser ->
            binding.tvSelectedCount.text = "已选${getSelectedList().size}张"
        }.setOnSelectAllListener { selectedCache, isSelectedAll ->
            if (binding.ivSelectedAll.isSelected != isSelectedAll) {
                binding.ivSelectedAll.isSelected = isSelectedAll
                binding.tvSelectedAll.text = if (isSelectedAll) "全不选" else "全选"
            }
        }.withType<ItemCameraBinding, Any?> {
            //该布局数据为null，无需绑定数据
        }.setOnClickListener { holder, data, position, view ->
            Toast.makeText(requireContext(), "点击拍照", Toast.LENGTH_SHORT).show()
        }.withType<ItemImageSelectedBinding, Int> { (holder, data, position, payloads) ->
            if (!payloads.contains("select")) {
                holder.binding.ivImage.load(data)
            }
            val index = this.adapter.getSelectedIndexAt(position)
            if (index < 0) {
                holder.binding.tvSelectedIndex.text = ""
                holder.binding.tvSelectedIndex.setBackgroundResource(R.drawable.bg_not_selected)
            } else {
                holder.binding.tvSelectedIndex.text = "${index + 1}"
                holder.binding.tvSelectedIndex.setBackgroundResource(R.drawable.bg_selected_position)
            }
        }.toAdapter()

    override fun FragmentSelectedBinding.initView() {
        //全选按钮点击事件
        llSelectedAll.setOnClickListener {
            if (adapter.isSelectAll())
                adapter.deselectAll()
            else
                adapter.selectAll()
        }
        
        //②为recyclerview设置adapter
        rvList.adapter = adapter

        //③获取数据后更新数据
        adapter.refresh(list)
    }

    //模拟数据
    private val list = arrayListOf(
        null,
        R.mipmap.snow1,
        R.mipmap.snow2,
        R.mipmap.snow3,
        R.mipmap.t1,
        R.mipmap.t2,
        R.mipmap.t3,
        R.mipmap.t4,
        R.mipmap.t5,
        R.mipmap.t6,
        R.mipmap.t7,
        R.mipmap.t8,
        R.mipmap.t9,
        R.mipmap.t10,
        R.mipmap.y1,
        R.mipmap.y2,
        R.mipmap.y3,
        R.mipmap.y4,
        R.mipmap.y5,
        R.mipmap.y6,
        R.mipmap.y7,
        R.mipmap.y8,
        R.mipmap.y9,
        R.mipmap.y10,
        )
}
```