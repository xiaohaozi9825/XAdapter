## 文本内容变化监听
> 给TextView子类绑定文本变化监听，adapter和provider都可以使用，推荐对provider使用


#### 核心方法

```kotlin
adapter.setOnTextChange(R.id.et_answer) { holder, data, position, view, text ->
            data.answer = text.toString()
         
}

provider.setOnTextChange(R.id.et_answer) { holder, data, position, view, text ->
            data.answer = text.toString()
}
```

##### 参数说明
- id：被监听的ViewId，必须是TextView或TextView子控件，如EditText，否则无效。
- listener：回调方法，状态改变时回调



