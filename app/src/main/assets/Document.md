# XAdapter使用文档
## XAdapter接入
#### 步骤1. 将 JitPack 添加到您的 build 文件中

在您工程 build.gradle 文件中添加:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

#### 步骤2. 添加依赖

[![](https://jitpack.io/v/xiaohaozi9825/XAdapter.svg)](https://jitpack.io/#xiaohaozi9825/XAdapter)


```
dependencies {
    implementation 'com.github.xiaohaozi9825:XAdapter:Tag'
}
```

#### 步骤3.启用ViewBinding或DataBinding

在项目 build.gradle 文件中android节点中添加，dataBinding和viewBinding至少启用一个

```
buildFeatures {
    dataBinding = true
    viewBinding = true
}
```

## XAdapter常用方法
此处只列举了一些常用方法，更多用法请在对应功能中查看参考代码。
#### 创建单布局Adapter
ItemVerseBinding 对应布局文件，VerseInfo 对应数据
```kotlin 
val adapter = createAdapter<ItemVerseBinding, VerseInfo> {(holder,data)->
                   //此处完成数据绑定逻辑
                }
```

#### 创建多布局Adapter
用withType指定不同的布局，返回ViewHolderProvider类型，最后需要调用toAdapter()方法切换回Adapter类型。
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

#### 添加点击事件
可以对Adapter或ViewHolderProvider类型添加点击事件。形参id指定对应的View，不指定id，则对这个item添加点击事件。
```kotlin
adapter.setOnClickListener(R.id.btn) { holder, data, position, view ->
    
}

provider.setOnClickListener(R.id.btn) { holder, data, position, view ->
    
}
```
长按、选中、文本变化等事件监听用法与点击事件类似，此处不在赘述。
#### 添加选择事件
参数permittedTypes指定需要添加选择事件的ViewType类型。
```kotlin
adapter.setOnItemSelectListener() { data, position, index, fromUser ->
               
}
```
#### 特殊布局
```kotlin
adapter.setEmpty<ItemEmptyBinding>()//设置空布局，无数据时展示
adapter.showDefaultPage<ItemLoadingBinding>()//显示缺省页
adapter.addHeader<ItemHomeHeaderBinding> ()//添加头布局
adapter.addFooter<ItemHomeFooterBinding>()//添加头布局
```

#### 侧滑删除、拖拽排序、侧滑菜单
侧滑删除、拖拽排序、侧滑菜单等功能与adapter本无太大关系，但是为了方便使用，这里做了统一封装。
```kotlin
adapter.swipeDelete()//对整个adapter执行侧滑操作
provide.swipeDelete()//只对当前类型执行侧滑操作

adapter.dragSort()//对整个adapter执行拖拽功能
provide.dragSort()//只对当前类型执行拖拽功能

adapter.swipeMenu()//添加侧滑删除功能，item根容器必须使用SwipeItemLayout。
```

#### 数据操作
数据操作必须在adapter初始化设置完成（如点击事件、长按事件等）后使用。
```kotlin
adapter.refresh()//刷新数据
adapter.add()//添加数据
adapter.remove()//删除数据
```

