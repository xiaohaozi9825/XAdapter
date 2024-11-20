package pw.xiaohaozi.xadapter.smart.utils

import android.util.SparseArray

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/11/20 14:29
 */
fun <T> SparseArray<T>.findKey(action: (key: Int, value: T) -> Boolean): Int? {
    for (index in 0 until size()) {
        val key = keyAt(index)
        val yalue = valueAt(index)
        if (action(key, yalue)) return key
    }
    return null
}

fun <T> SparseArray<T>.findValue(action: (key: Int, value: T) -> Boolean): T? {
    for (index in 0 until size()) {
        val key = keyAt(index)
        val value = valueAt(index)
        if (action(key, value)) return value
    }
    return null
}