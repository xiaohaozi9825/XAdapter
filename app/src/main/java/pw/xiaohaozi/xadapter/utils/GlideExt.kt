package pw.xiaohaozi.xadapter.utils

import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.io.File


fun ImageView.load(file: File) {
    Glide.with(this).load(file).into(this)
}
fun ImageView.load(file: String?) {
    Glide.with(this).load(file).into(this)
}

fun ImageView.load(@RawRes @DrawableRes res: Int) {
    Glide.with(this).load(res).into(this)
}


fun ImageView.load(res: Int, radius: Float = 0f) {

    //图形变换，先裁剪，后圆角
    val transformation = if (radius > 0f) {
        // 设置圆角半径（单位：像素）
        val cornerRadiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            radius,
            resources.displayMetrics
        ).toInt()

        MultiTransformation(
            CenterCrop(),  // 居中裁剪图片
            RoundedCorners(cornerRadiusPx) // 添加圆角
        )
    } else {
        CenterCrop() // 居中裁剪图片
    }

    Glide.with(context)
        .load(res)
        .apply(RequestOptions().transform(transformation))
        .into(this)
}

fun ImageView.loadCircle(res: Int) {

    //图形变换，先裁剪，后圆角
    val transformation = MultiTransformation(
        CenterCrop(),  // 居中裁剪图片
        CircleCrop() // 添加圆角
    )
    Glide.with(context)
        .load(res)
        .apply(RequestOptions().transform(transformation))
        .into(this)
}



