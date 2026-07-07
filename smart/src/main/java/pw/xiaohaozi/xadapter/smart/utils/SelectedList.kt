package pw.xiaohaozi.smartadapter.utils

import java.util.LinkedList

/**
 *
 * 描述：之前用的LinkedHashSetList，在移除 data class 时，如果数据发生改变，会导致无法删除
 * 作者：小耗子
 * 创建时间：2023/12/18 14:39
 */
class SelectedList<E> : LinkedList<E>() {
    /**
     * 与 [pw.xiaohaozi.xadapter.smart.proxy.SelectedProxy.selectionSame] 语义一致；未设置时退化为 equals。
     */
    var sameRule: ((E, E) -> Boolean)? = null

    private fun same(a: E, b: E): Boolean = sameRule?.invoke(a, b) ?: (a == b)

    private fun containsSame(element: E): Boolean = any { same(it, element) }

    override fun contains(element: E): Boolean = containsSame(element)

    override fun indexOf(element: E): Int {
        val it = listIterator()
        var index = 0
        while (it.hasNext()) {
            if (same(it.next(), element)) return index
            index++
        }
        return -1
    }

    override fun lastIndexOf(element: E): Int {
        val it = listIterator(size)
        var index = size - 1
        while (it.hasPrevious()) {
            if (same(it.previous(), element)) return index
            index--
        }
        return -1
    }

    override fun remove(element: E): Boolean {
        val it = iterator()
        while (it.hasNext()) {
            if (same(it.next(), element)) {
                it.remove()
                return true
            }
        }
        return false
    }

    override fun add(element: E): Boolean {
        return if (containsSame(element))
            false
        else super.add(element)
    }

    override fun add(index: Int, element: E) {
        if (containsSame(element))
            return
        else super.add(index, element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val newList = elements.filter { !containsSame(it) }
        return if (newList.isEmpty()) false
        else super.addAll(newList)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        val newList = elements.filter { !containsSame(it) }
        return if (newList.isEmpty()) false
        else super.addAll(index, elements)
    }

    override fun addFirst(e: E) {
        if (containsSame(e))
            return
        else super.addFirst(e)
    }

    override fun addLast(e: E) {
        if (containsSame(e))
            return
        else super.addLast(e)
    }
}
