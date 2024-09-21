package pw.xiaohaozi.xadapter.smart.proxy

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.holder.SmartHolder

/**
 * item选中事件
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/9/28 23:19
 */
interface SelectedProxy<Employer : XProxy<Employer>,  VB : ViewBinding, D> :
    XProxy<Employer> {
    val selectedCache: MutableCollection<D>

    //全选状态变化
    var selectedAllChanges: (Employer.(isSelectAll: Boolean) -> Unit)?

    //选中索引变化
    var selectedIndexChange: (Employer.(data: D, position: Int, index: Int) -> Unit)?

    //选中事件，一个item只能一个view响应选中事件
    var selectedListener: Pair<Int?, (Employer.(holder: SmartHolder<VB>?, data: D, position: Int, index: Int, view: View?) -> Unit)>?
    var borderCall: (Employer.(count: Int) -> Unit)?
    var MAX_CHECK_COUNT: Int?
    var isAllowCancel: Boolean

    /**
     * 设置选中事件监听
     * 如果需要显示选中顺序，必须调用 setOnCheckIndexChange 方法并传递 listener
     * @param clazz 指定可以被选中的数据类型，默认所有数据类型都可以被选中。多类型 item 建议指定对应 Class<D>
     * @param id 触发选中事件的 view，默认为item，多类型 item 建议指定对应view
     * @param listener 选中事件回调监听
     */
    fun setOnSelectedListener(
        @IdRes id: Int? = null,
        listener: Employer.(holder: SmartHolder<VB>?, data: D, position: Int, index: Int, trigger: View?) -> Unit
    ): Employer

    /**
     * 设置全选监听
     * @param listener
     */
    fun setOnSelectedAllChange(listener: Employer.(isCheckAll: Boolean) -> Unit): Employer

    /**
     * 设置item选择索引变化监听
     *
     * @param listener 如果该值为 null，则选中与取消是不会更新其他item。如果需要显示选中顺序，必须调用该方法并传递listener
     */
    fun setOnSelectedIndexChange(listener: (Employer.(data: D, position: Int, index: Int) -> Unit)?): Employer

    /**
     * 设置最大可选数
     * @param count 最大可选数
     * @param borderCall 超出最大可选数后的回调。如果该值为空，则会默认取消选中的第一个
     */
    fun setMaxSelectedCount(
        @IntRange(from = 1) count: Int,
        borderCall: (Employer.(count: Int) -> Unit)? = null
    ): Employer

    /**
     * 设置是否允许点击取消选中，对点击选中没有影响
     * @param isAllowCancel 如果该值为 true，则点击后可以取消选择。否则点击无法取消选择，但是可以通过调用cancelCheck()方法取消
     */
    fun allowCancel(isAllowCancel: Boolean = true): Employer

    /**
     * 设置指定 item 为选中状态
     * @param position
     */
    fun setSelected(@IntRange(from = 0) position: Int, trigger: View? = null): Employer

    /**
     * 设置指定 item 为选中状态
     * @param data
     */
    fun setSelected(data: D, trigger: View? = null): Employer

    /**
     * 设置指定 item 为取消选中状态
     * @param position
     */
    fun cancelSelected(@IntRange(from = 0) position: Int, trigger: View? = null): Employer

    /**
     * 设置指定 item 为取消选中状态
     * @param data
     */
    fun cancelSelected(data: D, trigger: View? = null): Employer

    /**
     * 是否已经全选
     * @return 所有item都选中返回 true ,否则返回 false
     */
    fun isSelectAll(): Boolean

    /**
     *  全选
     *  该方法不会检查最大可选数
     */
    fun selectAll(): Employer

    /**
     * 全不选
     */
    fun unselectAll(): Employer

    /**
     * 获取选中的数据
     * @return 被选中的数据集合
     */
    fun getSelectedDatas(): MutableList<D>

    /**
     * 指定索引下的item是否被选中
     * @param position
     * @return
     */
    fun isSelected(@IntRange(from = 0) position: Int): Boolean

    /**
     * 指定数据是否被选中
     * @param data
     * @return
     */
    fun isSelected(data: D): Boolean

    fun getSelectedIndex(position: Int): Int
    fun getSelectedIndex(data: D): Int
}
