package pw.xiaohaozi.smartadapter.utils

import java.util.LinkedList

/**
 *
 * 描述：之前用的LinkedHashSetList，在移除 data class 时，如果数据发生改变，会导致无法删除
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2023/12/18 14:39
 */
class SelectedList<E> : LinkedList<E>() {
    override fun add(element: E): Boolean {
        return if (contains(element))
            false
        else super.add(element)
    }

    override fun add(index: Int, element: E) {
        if (contains(element))
            return
        else super.add(index, element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val newList = elements.filter { !contains(it) }
        return if (newList.isEmpty()) false
        else super.addAll(newList)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        val newList = elements.filter { !contains(it) }
        return if (newList.isEmpty()) false
        else super.addAll(index, elements)
    }

    override fun addFirst(e: E) {
        if (contains(e))
            return
        else super.addFirst(e)
    }

    override fun addLast(e: E) {
        if (contains(e))
            return
        else super.addLast(e)
    }
}