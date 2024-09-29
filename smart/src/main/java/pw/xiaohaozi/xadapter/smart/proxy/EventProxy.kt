package pw.xiaohaozi.xadapter.smart.proxy

import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.holder.XHolder

typealias OnItemClickListener<Employer, VB, D> = Employer.(holder: XHolder<VB>, data: D, position: Int, view: View) -> Unit
typealias OnItemLongClickListener<Employer, VB, D> = Employer.(holder: XHolder<VB>, data: D, position: Int, view: View) -> Boolean
typealias OnItemCheckedChangeListener<Employer, VB, D> = Employer.(holder: XHolder<VB>, data: D, position: Int, view: CompoundButton, isCheck: Boolean) -> Unit
typealias OnItemTextChange<Employer, VB, D> = Employer.(holder: XHolder<VB>, data: D, position: Int, view: TextView, text: CharSequence?) -> Unit

/**
 * 事件监听接口
 * 描述：负责View事件监听，入点击事件，长按事件，选中事件，开关事件，EditView文本变化监听，进度条变化监听等
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/9/28 23:19
 */
interface EventProxy<Employer : XProxy<Employer>, VB : ViewBinding, D> : XProxy<Employer> {
    val clickListenerMap: HashMap<Int?, OnItemClickListener<Employer, VB, D>>
    val longClickListenerMap: HashMap<Int?, OnItemLongClickListener<Employer, VB, D>>
    val checkedChangeListener: HashMap<Int?, OnItemCheckedChangeListener<Employer, VB, D>>
    val textChangeMap: HashMap<Int?, OnItemTextChange<Employer, VB, D>>

    /**
     * 设置点击事件
     * @param id 需要被点击的 view id ，如果为空，则给 item 设置点击事件
     * @param listener 点击事件回调
     */
    fun setOnClickListener(
        @IdRes id: Int? = null,
        listener: OnItemClickListener<Employer, VB, D>
    ): Employer

    /**
     * 设置长按事件
     * @param id 需要被长按的 view id ，如果为空，则给 item 设置长按事件
     * @param listener 长按事件回调
     */
    fun setOnLongClickListener(@IdRes id: Int? = null, listener: OnItemLongClickListener<Employer, VB, D>): Employer

    fun setOnCheckedChangeListener(id: Int? = null, listener: OnItemCheckedChangeListener<Employer, VB, D>): Employer

    fun setOnTextChange(id: Int? = null, listener: OnItemTextChange<Employer, VB, D>): Employer

}

