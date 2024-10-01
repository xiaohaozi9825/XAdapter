package pw.xiaohaozi.xadapter.smart.proxy

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.holder.XHolder

/**
 * 全选状态监听
 */
typealias OnSelectAllListener<Employer> = Employer.(isSelectAll: Boolean) -> Unit
/**
 * 选择操作监听
 */
typealias OnItemSelectListener<Employer, VB, D> = Employer.(holder: XHolder<VB>?, data: D, position: Int, index: Int, fromUser: Boolean) -> Unit

/**
 * 选择状态变化监听
 */
typealias OnItemSelectStatusChanges<Employer, D> = Employer.(data: D, position: Int, index: Int) -> Unit

/**
 * item选择事件
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/9/28 23:19
 */
interface SelectedProxy<Employer : XProxy<Employer>, VB : ViewBinding, D> :
    XProxy<Employer> {
    val selectedCache: MutableCollection<D>

    //全选状态变化
    var selectAllChanges: OnSelectAllListener<Employer>?

    //选中状态变化监听
    var itemSelectStatusChanges: OnItemSelectStatusChanges<Employer, D>?

    //选中事件，一个item只能一个view响应选中事件
    var itemSelectListener: Triple<Int?, String?,OnItemSelectListener<Employer, VB, D>>?

    //最大可选数
    var maxSelectCount: Int?

    //超出最大数后是否自动取消，默认自动取消
    var isAutoCancel: Boolean

    //是否允许取消，默认允许
    var isAllowCancel: Boolean

    /**
     * 设置选中事件监听
     * @param id 触发选中事件的 view，默认为item
     * @param listener 选中事件回调监听
     */
    fun setOnItemSelectListener(
        id: Int? = null,
        payload: String? = null,
        listener: OnItemSelectListener<Employer, VB, D>
    ): Employer

    /**
     * 设置全选监听
     * @param listener
     */
    fun setOnSelectAllListener(listener: OnSelectAllListener<Employer>): Employer

    /**
     * 设置item选择状态变化监听
     *
     * @param listener
     */
    fun setOnItemSelectStatusChanges(listener: OnItemSelectStatusChanges<Employer, D>): Employer

    /**
     * 设置最大可选数
     * @param count 最大可选数
     */
    fun setMaxSelectCount(count: Int): Employer

    /**
     * 超出最大可选数时，是否自动取消
     * @param isAutoCancel
     */
    fun isAutoCancel(isAutoCancel: Boolean = true): Employer

    /**
     * 设置是否允许点击取消选中，设置后无法取消选中
     * @param isAllowCancel 如果该值为 true，则点击后可以取消选择。否则点击无法取消选择
     */
    fun isAllowCancel(isAllowCancel: Boolean = true): Employer

    /**
     * 修改item选择状态
     * @param position
     * @param isSelect 是否选择
     * @param fromUser 选择事件是否由用户发起的
     * @return 选中索引
     */
    fun setSelectAt(position: Int, isSelect: Boolean, fromUser: Boolean = false): Int

    /**
     * 设置指定 item 为选中状态
     * @param data
     * @param isSelect 是否选择
     * @param fromUser 选择事件是否由用户发起的
     * @return 选中索引
     */
    fun setSelect(data: D, isSelect: Boolean, fromUser: Boolean = false): Int


    /**
     * 是否已经全选
     * @return 所有item都选中返回 true ,否则返回 false
     */
    fun isSelectAll(): Boolean

    /**
     *  全选
     */
    fun selectAll(): Int

    /**
     * 取消全选
     */
    fun deselectAll(): Int

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
    fun isSelectedAt(position: Int): Boolean

    /**
     * 指定数据是否被选中
     * @param data
     * @return
     */
    fun isSelected(data: D): Boolean

    /**
     * 获取选中的序号，从0开始，-1表示未选中
     */
    fun getSelectedIndexAt(position: Int): Int

    /**
     * 获取选中的序号，从0开始，-1表示未选中
     */
    fun getSelectedIndex(data: D): Int
}

