package pw.xiaohaozi.xadapter.smart.entity

/**
 * 特殊布局枚举类
 * 描述：优先级：头布局&较不具>缺省页>数据类>空布局
 *
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/7 8:51
 */
sealed class SpecialLayoutEnum(val tag: Any)
class HEADER(tag: Any) : SpecialLayoutEnum(tag)//头布局，可展示多个
class FOOTER(tag: Any) : SpecialLayoutEnum(tag)//脚布局，可展示多个
object EMPTY : SpecialLayoutEnum("")//缺省页：空布局，最多1个
class DEFAULT_PAGE(tag: Any) : SpecialLayoutEnum(tag)//通用缺省页，可设置多个，但最多展示1个
