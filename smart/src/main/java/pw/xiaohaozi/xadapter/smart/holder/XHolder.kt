package pw.xiaohaozi.xadapter.smart.holder


import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/8/10 20:09
 */
open class XHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

