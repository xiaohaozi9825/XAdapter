## 协程作用域
> adapter、provider、viewHolder都提供了相应的协程作用域，provider与adapter协程作用域一样，viewHolder协程会在viewHolder回收时取消


#### 参考代码

```kotlin
.withType<ItemImageSelectedBinding, Int> { (holder, data, position, payloads) ->
    if (!payloads.contains("select")) {
        //模拟耗时操作，在滑动时能明显感觉到卡顿
        //val bitmap = BitmapFactory.decodeResource(resources, data)
        //holder.binding.ivImage.setImageBitmap(bitmap)

        //使用协程，将耗时操作切换到其他线程
        holder.launch(IO) {
            val bitmap = BitmapFactory.decodeResource(resources, data)
            withContext(Main) {
                holder.binding.ivImage.setImageBitmap(bitmap)
            }
        }
    }         
}
```




