## 侧滑删除
> 侧滑删除item


#### 核心方法

```kotlin
//对整个adapter添加侧滑删除功能
adapter.swipeDelete()

//只对当前类型添加侧滑删除功能
provider.swipeDelete()
```

##### 参数说明
- threshold 设置用户在拖拽视图时应该移动视图的比例。在视图移动到这个位置之后，ItemTouchHelper开始检查视图下方是否有可能的删除。一个浮点值，表示视图大小的百分比。缺省值为。1f。
- flags 触发方向
- start 开始拖拽
- end 结束拖拽（松开手就会调用）
- onMove 被拖拽的item多拽到其他item位置上是调用,该参数会替换掉现有的onMove逻辑



