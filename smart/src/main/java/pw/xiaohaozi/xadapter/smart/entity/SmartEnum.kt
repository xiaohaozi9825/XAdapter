package pw.xiaohaozi.xadapter.smart.entity

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/7 8:51
 */
sealed class SmartEnum(val tag: String)
class HEADER(tag: String) : SmartEnum(tag)
class FOOTER(tag: String) : SmartEnum(tag)
object EMPTY : SmartEnum("")
object ERROR : SmartEnum("")
