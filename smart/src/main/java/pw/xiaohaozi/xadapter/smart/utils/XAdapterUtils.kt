package pw.xiaohaozi.xadapter.smart.utils


/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/9/25 9:01
 */


////https://wenku.baidu.com/view/60dbe7fd53e2524de518964bcf84b9d528ea2cbd.html
//inline fun <reified T : Any> new(): T {
//    val clazz = T::class.java
//    val create = clazz.getDeclaredConstructor()
//    create.isAccessible = true
//    return create.newInstance()
//}

/**
 * 从start开始，移除集合中count个元素
 */
fun MutableList<*>.remove(start: Int, count: Int) {
    var index = 0
    val itr = this.listIterator(start)
    while (itr.hasNext()) {
        itr.next()
        if (index <= count) {
            itr.remove()
            if (++index == count) return
        }
    }
}