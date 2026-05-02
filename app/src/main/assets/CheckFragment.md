## 选中状态改变监听
> 特殊布局，如头布局，脚布局，空布局、错误布局


#### 核心方法

```kotlin
adapter.setOnCheckedChangeListener(R.id.rb_option_a) { holder, data, position, view, isCheck ->
            
}

provider.setOnCheckedChangeListener(R.id.rb_option_a) { holder, data, position, view, isCheck ->
            
}
```

##### 参数说明
- id：被监听的ViewId，必须是CompoundButton的子类，否则无效。
- listener：回调方法，状态改变时回调



