package pw.xiaohaozi.xadapter.info

import androidx.fragment.app.Fragment

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/3 19:19
 */
data class HomeInfo(
    var label: String,
    var msg: String,
    val url: Int,
    val clazz: Class<out Fragment>? = null
)