## 拖拽排序
> 长按拖拽排序


#### 核心方法

```kotlin
//对整个adapter所有item增加拖拽排序功能
adapter.dragSort()

//知道当前类型进行拖拽排序功能
provider.dragSort()
```

##### 参数说明
- threshold 设置用户在拖拽视图时应该移动视图的比例。在视图移动到这个位置之后，ItemTouchHelper开始检查视图下方是否有可能的删除。一个浮点值，表示视图大小的百分比。缺省值为。1f。
- flags 触发方向
- start 开始拖拽
- end 结束拖拽（松开手就会调用）
- onMove 被拖拽的item多拽到其他item位置上是调用,该参数会替换掉现有的onMove逻辑
- swap 当两个item交换时调用



