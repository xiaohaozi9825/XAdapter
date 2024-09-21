package pw.xiaohaozi.xadapter

import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import pw.xiaohaozi.xadapterdemo.utils.anima


/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/22 23:56
 */


@BindingAdapter("load")
fun setImage(view: ImageView, url: String) {
    view.load(url)
}


@BindingAdapter("load")
fun setImage(view: ImageView, @IdRes url: Int) {
    view.load(url)
}
@BindingAdapter("circle")
fun setImageCircle(view: ImageView, url: String) {
    view.load(url) {
        transformations(CircleCropTransformation())  //圆形图
    }
}
@BindingAdapter("circle")
fun setImageCircle(view: ImageView, url: Int) {
    view.load(url) {
        transformations(CircleCropTransformation())  //圆形图
    }
}
@BindingAdapter("click")
fun onClick(view: View, listener: View.OnClickListener) {
    view.setOnClickListener(listener)
    view.anima()
}

@BindingAdapter("anima")
fun anima(view: View, value: Int = 6) {
    val scale = view.resources.displayMetrics.density
    view.anima((value * scale + 0.5f).toInt())
}
