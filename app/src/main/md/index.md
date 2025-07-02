# XAdapter 框架
在Android项目开发过程中，我们会大量使用RecyclerView控件，而每个RecyclerView都需要一个与之对应的Adapter，这会花费我们大量的开发时间。而XAdapter框架则将Adapter创建、ViewHolder创建、以及大量常用方法封装到了一起，使得每个功能只需要调用一个方法即可完成，最大程度提高开发效率。


## XAdapter的优势
充分利用kotlin语法特性，结合ViewBinding或DataBinding，基本可以一个方法实现一个功能。用最简单的方式，实现复杂的功能，极大提高开发效率。

- 单布局一个方法即可创建，无需使用继承。
- 多布局自动计算itemType，无需手动判断。
- 封装了对数据增、删、改、查、交换、刷新等操作方法。
- 封装了View 点击事件、长按事件、选中状态、文本变化等监听方法。
- 封装了Item选择操作，可设置选择数量、全选、全部选等方法。
- 集成了Differ刷新算法，可实现ListAdapter类似效果。
- 封装了特殊布局，如头布局、脚布局、空布局、缺省页、分组等。
- 封装了侧滑删除、拖拽排序、侧滑菜单等常用操作。
- 使用泛型约束，回调方法中数据类型自动转换，省去开发者频繁校验类型。
- 集成了协程，ViewHolder回收时自动取消协程。
- 集成了lifecycle生命周期管理。
- 方法返回adapter，方便开发者链式调用。
- 回调方法this指向adapter。

## XAdapter的功能

#### smart模块
* adapter创建：单布局创建、多布局创建。
* 常用事件封装：点击事件、长按事件、文本变化监听、选中状态变化监听。
* 封装Item选择：单选、多选、全选、全不选、显示选择顺序等。
* 特殊布局封装：头布局、脚布局、空布局、缺省页布局等。
* Item滑动处理：侧滑删除、拖拽排序、侧滑菜单。
* 数据操作相关：数据更新、数据增删改查等。

#### node模块
* nodeAdapter创建：单布局创建、多布局创建。
* node数据操作相关：数据增删改查。
* node展开与折叠。

## 方案对比

### 创建单布局对比

#### 使用原始方法创建
在不借助第三方框架的情况下，我们使用一个Adapter需要有如下步骤：

* 继承ViewHolder类
```kotlin 
class MyViewHolder(itemView: View) : ViewHolder(itemView) {

}
```

* 继承Adapter类，重写onCreateViewHolder()和onBindViewHolder()方法
```kotlin 
class MyAdapter : Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder()
    }
    override fun getItemCount(): Int {
        //返回item数量
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //此处完成数据绑定逻辑
    }
}
```

* 在Activity中创建MyAdapter实例
```kotlin 
 val adapter = MyAdapter()
```

#### 使用优秀的第三方库创建
比如BaseRecyclerViewAdapterHelper框架。


* 继承BaseQuickAdapter类，重写convert()方法
```java
public class DataBindingAdapter extends BaseQuickAdapter<Movie, BaseDataBindingHolder<ItemMovieBinding>> {
    public DataBindingAdapter() {
        super(R.layout.item_movie);
    }
    @Override
    protected void convert(@NotNull BaseDataBindingHolder<ItemMovieBinding> holder, Movie item) {
       //此处完成数据绑定逻辑
    }
}
```

* 在Activity中创建MyAdapter实例
```kotlin 
 val adapter = DataBindingAdapter()
```

#### 使用XAdapter框架
只需要一个方法即可。对，你没看错，就只需要一个方法
```kotlin 
val adapter = createAdapter<ItemVerseBinding, VerseInfo> {(holder,data)->
                   //此处完成数据绑定逻辑
                }
```

### 创建多布局对比

#### 使用原始方法创建
在不借助第三方框架的情况下，我们使用一个Adapter需要有如下步骤：

* 继承ViewHolder类
```kotlin 
class MyViewHolder0(itemView: View) : ViewHolder(itemView) {

}
```

* 继承Adapter类，重写onCreateViewHolder()和onBindViewHolder()方法
```kotlin 
class MyAdapter : Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> MyViewHolder0()
            1 -> MyViewHolder1()
            else -> MyViewHolder2()
        }
    }
    override fun getItemCount(): Int {
        //返回item数量
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> {}//此处完成数据绑定逻辑
            1 -> {}//此处完成数据绑定逻辑
            else -> {}//此处完成数据绑定逻辑
        }
    }
    override fun getItemViewType(position: Int): Int {
        return when (position % 3) {
            0 -> 0
            1 -> 1
            else -> 2
        }
    }
}
```


* 在Activity中创建MyAdapter实例
```kotlin 
 val adapter = MyAdapter()
```

#### 使用优秀的第三方库创建
比如BaseRecyclerViewAdapterHelper框架。
* 每种类型都继承BaseItemProvider类并实现绑定逻辑
```java
public class ImgItemProvider extends BaseItemProvider<ProviderMultiEntity> {
    @Override
    public int getItemViewType() {
        return ProviderMultiEntity.IMG;
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_image_view;
    }
    @Override
    public void convert(@NonNull BaseViewHolder helper, @Nullable ProviderMultiEntity data) {
       //此处完成数据绑定逻辑
    }
}
```

* 创建BaseProviderMultiAdapter子类，添加并实现各Provider之间的对应关系
```java
public class ProviderMultiAdapter extends BaseProviderMultiAdapter<ProviderMultiEntity> {
    public ProviderMultiAdapter() {
        super();
        addItemProvider(new ImgItemProvider());
        addItemProvider(new TextImgItemProvider());
        addItemProvider(new TextItemProvider());
    }
    @Override
    protected int getItemType(@NotNull List<? extends ProviderMultiEntity> data, int position) {
        switch (position % 3) {
            case 0:
                return ProviderMultiEntity.IMG;
            case 1:
                return ProviderMultiEntity.TEXT;
            case 2:
                return ProviderMultiEntity.IMG_TEXT;
            default:
                break;
        }
        return 0;
    }
}
```

* 在Activity中创建ProviderMultiAdapter实例
```kotlin 
 val adapter = ProviderMultiAdapter()
```

#### 使用XAdapter框架
```kotlin
val adapter = createAdapter()
            .withType<ItemVerseBinding, VerseInfo> { (holder, data) ->
                //此处完成数据绑定逻辑
            }
            .withType<ItemImageCardBinding, Int> { (holder, data) ->
                //此处完成数据绑定逻辑
            }
            .toAdapter()
```

## XAdapter相关地址

demo体验：[https://www.pgyer.com/7kPKon2W](https://www.pgyer.com/7kPKon2W)

github：[https://github.com/xiaohaozi9825/XAdapter](https://github.com/xiaohaozi9825/XAdapter)

gitee：[https://gitee.com/xiaohaozi9825/xadapter](https://gitee.com/xiaohaozi9825/xadapter)

简书：[https://www.jianshu.com/p/936be339b378?v=1735022668540](https://www.jianshu.com/p/936be339b378?v=1735022668540)