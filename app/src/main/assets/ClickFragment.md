## 点击事件监听
> 给item中指定View添加点击事件，adapter和provider都可以使用，推荐对provider使用


#### 核心方法

```kotlin
adapter.setOnClickListener(R.id.btn) { holder, data, position, view ->
    
}

provider.setOnClickListener(R.id.btn) { holder, data, position, view ->
    
}
```

##### 参数说明
- id：被监听的ViewId。可选参数，默认为itemRoot
- listener：回调方法，状态改变时回调



