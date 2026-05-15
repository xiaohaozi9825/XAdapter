package pw.xiaohaozi.xadapterdemo.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 *
 * 描述：
 * 作者：小耗子
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/23 19:15
 */
object GsonUtil {

//    inline fun <reified T> String.fromJson(): T = Gson().fromJson(this)
    fun Any.toJson(): String = Gson().toJson(this)

    //type 泛型扩展
    inline fun <reified T> String.fromJson(): T =
        Gson().fromJson(this, object : TypeToken<T>() {}.type)

}
