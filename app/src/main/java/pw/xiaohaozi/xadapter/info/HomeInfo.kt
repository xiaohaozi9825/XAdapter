package pw.xiaohaozi.xadapter.info

import androidx.fragment.app.Fragment

/**
 *
 * 描述：
 * 作者：小耗子
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/3 19:19
 */
data class HomeInfo(
    var label: String,//标题
    var msg: String,//描述
    val url: Int,//图片id
    val clazz: Class<out Fragment>? = null,//跳转的界面Fragment
    val markdownName: String? = null,//md文件名，如果没有指定，则是对应fragment名
)
