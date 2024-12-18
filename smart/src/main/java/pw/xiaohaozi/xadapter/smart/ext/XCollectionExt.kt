package pw.xiaohaozi.xadapter.smart.ext

import androidx.annotation.IntRange

/**
 * 从start开始，移除集合中count个元素
 */
fun <T> MutableList<T>.removeRange(start: Int, count: Int): MutableList<T> {
    var index = 0
    val itr = this.listIterator(start)
    val removes = mutableListOf<T>()
    while (itr.hasNext()) {
        val item = itr.next()
        removes.add(item)
        if (index <= count) {
            itr.remove()
            if (++index == count) break
        }
    }
    return removes
}

/**
 * 删除制定制定位置的值
 * data class 中的值改变后，LinkedHashSet无法被删除
 */
fun <T> LinkedHashSet<T?>.removeAt(@IntRange(from = 0) index: Int) {
    val iterator = this.iterator()
    var i = 0
    while (iterator.hasNext()) {
        iterator.next()
        if (index == i) {
            iterator.remove()
            return
        }
        i++
    }
}

fun <T> LinkedHashSet<T>.get(@IntRange(from = 0) index: Int): T? {
    var t: T? = null
    this.forEachIndexed { i, data ->
        if (index == i) t = data
        return@forEachIndexed
    }
    return t
}