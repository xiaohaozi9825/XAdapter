package pw.xiaohaozi.xadapter.fragment.example

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentGoodsBinding
import pw.xiaohaozi.xadapter.databinding.ItemCategoryBinding
import pw.xiaohaozi.xadapter.databinding.ItemCategoryGroupBinding
import pw.xiaohaozi.xadapter.databinding.ItemGoodsBinding
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smart.ext.singleSelect
import pw.xiaohaozi.xadapter.utils.load


class GoodsFragment : Fragment() {
    private lateinit var binding: FragmentGoodsBinding
    private val TAG = "FriendFragment"

    //左边分类列表适配器
    @SuppressLint("SetTextI18n")
    private val categoryAdapter = createAdapter()
        //增加一个类型，添加在数据最后方，让最后一个可见item选中时，看起来有圆角效果
        .withType<ItemCategoryBinding, String> { (holder, _, position) ->
            //如果签名一个item为选中状态，则设置右上角为圆角。否则颜色为透明色
            if (position > 0 && adapter.isSelectedAt(position - 1)) {
                holder.binding.root.setBackgroundColor(Color.WHITE)
                holder.binding.tvName.setBackgroundResource(R.drawable.item_top_right_radius_white)
            } else {
                holder.binding.root.setBackgroundColor(Color.TRANSPARENT)
                holder.binding.tvName.setBackgroundColor(Color.TRANSPARENT)
            }
        }
        //显示知道itemType，方便只对该类型添加选择事件
        .withType<ItemCategoryBinding, CategoryInfo>(itemType = 1) { (holder, data, position, player) ->
            holder.binding.tvName.text = data.name
            //根据选中状态，改变item背景颜色和文字颜色
            if (adapter.isSelected(data)) {
                holder.binding.root.setBackgroundColor(Color.WHITE)
                holder.binding.tvName.setBackgroundColor(Color.TRANSPARENT)
                holder.binding.tvName.setTextColor(requireContext().resources.getColor(R.color.theme))
            } else {
                holder.binding.tvName.setTextColor(Color.parseColor("#FF666666"))

                //给选中的item上下设置圆角
                if (position < adapter.getDataList().size - 1 && adapter.isSelectedAt(position + 1)) {
                    holder.binding.root.setBackgroundColor(Color.WHITE)
                    holder.binding.tvName.setBackgroundResource(R.drawable.item_bottom_right_radius_white)
                } else if (position > 0 && adapter.isSelectedAt(position - 1)) {
                    holder.binding.root.setBackgroundColor(Color.WHITE)
                    holder.binding.tvName.setBackgroundResource(R.drawable.item_top_right_radius_white)
                } else {
                    holder.binding.root.setBackgroundColor(Color.TRANSPARENT)
                    holder.binding.tvName.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
        .toAdapter()
        //设置为单选。permittedTypes = arrayOf(1) 只对itemType == 1 的item添加选择事件
        .singleSelect(permittedTypes = arrayOf(1)) { data, position, index, fromUser ->
            //同时更新前后item，改变圆角
            if (position > 0) notifyItemChanged(position - 1)
            if (position < getDataList().size - 1) notifyItemChanged(position + 1)

            //设置右边吸顶位置和内容
            if (fromUser && index != -1) {
                binding.rvGoods.stopScroll()//停止商品列表滑动动画
                val goodsPosition = goodsAdapter.getDataList().indexOf(data)
                val layoutManager = binding.rvGoods.layoutManager as LinearLayoutManager
                //将对应商品组滑动到最顶部，此处有大概120ms的动画
                layoutManager.scrollToPositionWithOffset(goodsPosition, 0)
                //等动画结束后，更新吸顶内容和位置
                binding.rvGoods.postDelayed({
                    val visibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val completelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val visibleNode = goodsAdapter.getDataList()[visibleItemPosition]
                    val completelyVisibleNode = goodsAdapter.getDataList()[completelyVisibleItemPosition]

                    //设置吸顶内容
                    val friendGroup = when (visibleNode) {
                        is CategoryInfo -> visibleNode
                        is GoodsInfo -> visibleNode.getParentNodeEntity()
                        else -> null
                    }
                    setCeilingMounted(friendGroup, false)

                    //设置吸顶位置
                    if (completelyVisibleNode is CategoryInfo) {
                        val y = getY(completelyVisibleItemPosition)
                        if (y <= binding.category.height) {//跟随
                            binding.category.translationY = (y - binding.category.height.toFloat())
                        } else {//悬停
                            binding.category.translationY = 0f
                        }
                    } else {//悬停
                        binding.category.translationY = 0f
                    }

                }, (binding.rvGoods.itemAnimator?.changeDuration ?: 120) + 30)//动画时间一般是120ms

            }
        }

    //右边商品列表适配器
    @SuppressLint("SetTextI18n")
    private val goodsAdapter = nodeAdapter()
        .withType<ItemCategoryGroupBinding, CategoryInfo> { (holder, data) ->
            holder.binding.root.text = data.name
        }
        .withType<ItemGoodsBinding, GoodsInfo> { (holder, data) ->
            holder.binding.tvPrick.text = data.prickHtml()
            holder.binding.tvName.text = data.name
            holder.binding.ivImage.load(data.image, 8f)
        }
        .toAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoodsBinding.inflate(inflater)
        binding.rvCategory.adapter = categoryAdapter
        binding.rvGoods.adapter = goodsAdapter
        binding.rvGoods.addOnScrollListener(GoodsScrollListener())
        categoryAdapter.refresh(dataList + "")
        goodsAdapter.refresh(dataList)
        //分类列表默认选中第一个
        categoryAdapter.setSelectAt(0, true)
        //初始化吸顶视图
        setCeilingMounted(dataList.first(), false)
        return binding.root
    }

    /**
     * 设置吸顶内容
     */
    @SuppressLint("SetTextI18n")
    fun setCeilingMounted(data: CategoryInfo?, isScroll: Boolean) {
        data ?: return
        binding.category.text = data.name
        //商品列表滑动触发修改吸顶效果时，需要更改分类列表选择状态
        if (isScroll && !categoryAdapter.isSelected(data)) {
            categoryAdapter.setSelect(data, true)
        }

    }

    //获取当前位置View的Y坐标值
    private fun getY(position: Int): Int {
        val layoutManager = binding.rvGoods.layoutManager as LinearLayoutManager
        val itemView = layoutManager.findViewByPosition(position);
        if (itemView != null) {
            val itemLocation = IntArray(2);
            val recyclerLocation = IntArray(2);
            itemView.getLocationOnScreen(itemLocation); // Item 的屏幕坐标
            binding.rvGoods.getLocationOnScreen(recyclerLocation); // RecyclerView 的屏幕坐标
            return itemLocation[1] - recyclerLocation[1] // 相对 Y 坐标
        }
        return 0
    }

    /**
     * recyclerView 滑动监听，实现吸顶效果
     */
    inner class GoodsScrollListener : OnScrollListener() {
        //分组Item高度
        private val groupHeight by lazy { binding.category.height }
        private val layoutManager by lazy { binding.rvGoods.layoutManager as LinearLayoutManager }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val visibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val completelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            val visibleNode = goodsAdapter.getDataList()[visibleItemPosition]
            val completelyVisibleNode = goodsAdapter.getDataList()[completelyVisibleItemPosition]
            if (dy > 0) {//上滑
                if (visibleNode is CategoryInfo) {
                    setCeilingMounted(visibleNode, true)
                }
                //第一个完全可见的item是分组类型，item滑到顶部时，吸顶跟随向上滑动，直至item到达顶部，吸顶会恢复到Y坐标为0的位置
                if (completelyVisibleNode is CategoryInfo) {
                    val y = getY(completelyVisibleItemPosition)
                    if (y <= groupHeight) {//跟随上滑
                        binding.category.translationY = (y - groupHeight).toFloat()
                    } else {//悬停
                        binding.category.translationY = 0f
                    }
                } else {//悬停
                    binding.category.translationY = 0f
                }

            } else if (dy < 0) {//下滑
                val friendGroup = when (visibleNode) {
                    is CategoryInfo -> visibleNode
                    is GoodsInfo -> visibleNode.getParentNodeEntity()
                    else -> null
                }
                setCeilingMounted(friendGroup, true)
                //第一个完全可见的item是分组类型，吸顶跟随item向下滑动，直至吸顶完全展示，最后停留在Y坐标为0的位置
                if (completelyVisibleNode is CategoryInfo) {
                    val y = getY(completelyVisibleItemPosition)
                    if (y <= binding.category.height) {//跟随
                        binding.category.translationY = (y - groupHeight).toFloat()
                    } else {//悬停
                        binding.category.translationY = 0f
                    }
                } else {//悬停
                    binding.category.translationY = 0f
                }
            }
        }
    }

    val dataList = mutableListOf(
        CategoryInfo(
            "轻乳茶", mutableListOf(
                GoodsInfo(R.mipmap.snow1, "鲜萃轻轻红茶", "19.2"),
                GoodsInfo(R.mipmap.snow2, "鲜萃轻轻茉莉", "19.2"),
            )
        ),
        CategoryInfo(
            "果蔬茶", mutableListOf(
                GoodsInfo(R.mipmap.snow3, "小桑葚果蔬茶", "17.98"),
                GoodsInfo(R.mipmap.t1, "羽衣轻体果蔬茶", "19.2"),
            )
        ),
        CategoryInfo(
            "非咖/果茶", mutableListOf(
                GoodsInfo(R.mipmap.t1, "生椰杨枝甘露", "21.12"),
                GoodsInfo(R.mipmap.t2, "葡萄柠檬茶", "21.12"),
                GoodsInfo(R.mipmap.t3, "轻咖柠檬茶", "21.12"),
                GoodsInfo(R.mipmap.t4, "轻咖椰子水", "21.12"),
                GoodsInfo(R.mipmap.t5, "红茶柠檬茶", "19.2"),
                GoodsInfo(R.mipmap.t6, "抹茶丝绒拿铁", "21.15"),
                GoodsInfo(R.mipmap.t7, "抹茶好喝椰", "21.7"),
                GoodsInfo(R.mipmap.t8, "芦荟椰子水", "19"),
                GoodsInfo(R.mipmap.t9, "苹果C冰茶", "19"),
                GoodsInfo(R.mipmap.t10, "荔枝乳酸菌冰茶", "19"),
                GoodsInfo(R.mipmap.y1, "橙C冰茶", "19"),
                GoodsInfo(R.mipmap.y2, "柚C冰茶", "17.9"),
                GoodsInfo(R.mipmap.y3, "纯牛奶", "22"),
            )
        ),
        CategoryInfo(
            "美式家族", mutableListOf(
                GoodsInfo(R.mipmap.y3, "痛苦面具·酸角美式", "17.98"),
                GoodsInfo(R.mipmap.y4, "苹果C美式", "17.98"),
                GoodsInfo(R.mipmap.y5, "多肉葡萄咖", "17.98"),
                GoodsInfo(R.mipmap.y6, "小黄油美式", "17.98"),
                GoodsInfo(R.mipmap.y7, "荔枝冰萃美式", "17.98"),
                GoodsInfo(R.mipmap.y8, "柠C美式", "17.98"),
                GoodsInfo(R.mipmap.y9, "芦荟美式", "17.98"),
                GoodsInfo(R.mipmap.y10, "椰青冰萃美式", "17.98"),
                GoodsInfo(R.mipmap.snow1, "乳酸菌美式", "17.98"),
                GoodsInfo(R.mipmap.snow2, "茉莉花香美式", "17.98"),
            )
        ),
        CategoryInfo(
            "风味拿铁", mutableListOf(
                GoodsInfo(R.mipmap.t1, "马斯卡彭生酪拿铁", "17.98"),
                GoodsInfo(R.mipmap.t2, "米乳拿铁", "17.98"),
                GoodsInfo(R.mipmap.t3, "奶皮子拿铁", "17.98"),
                GoodsInfo(R.mipmap.t4, "双椰拿铁", "17.98"),
                GoodsInfo(R.mipmap.t5, "小白巧拿铁", "17.98"),
                GoodsInfo(R.mipmap.t6, "阿克苏苹果拿铁", "17.98"),
                GoodsInfo(R.mipmap.t7, "琯溪蜜柚拿铁", "17.98"),
                GoodsInfo(R.mipmap.t8, "熊猫陨石拿铁", "17.98"),
                GoodsInfo(R.mipmap.t9, "丝绒拿铁", "17.98"),
                GoodsInfo(R.mipmap.y1, "燕麦拿铁", "17.98"),
                GoodsInfo(R.mipmap.y2, "生椰丝绒拿铁", "17.98"),
                GoodsInfo(R.mipmap.y3, "香草丝绒拿铁", "17.98"),
            )
        ),
        CategoryInfo(
            "咖啡轻食", mutableListOf(
                GoodsInfo(R.mipmap.snow3, "拿铁 早餐", "17.98"),
                GoodsInfo(R.mipmap.y4, "美式 早餐", "17.98"),
                GoodsInfo(R.mipmap.y5, "轻享·下午茶", "17.98"),
            )
        ),
        CategoryInfo(
            "大师咖啡", mutableListOf(
                GoodsInfo(R.mipmap.t1, "拿铁", "20.88"),
                GoodsInfo(R.mipmap.t2, "焦糖玛朵奇", "25.6"),
                GoodsInfo(R.mipmap.t3, "香草拿铁", "21.12"),
                GoodsInfo(R.mipmap.t4, "精萃奥瑞白", "25.6"),
                GoodsInfo(R.mipmap.t5, "卡布奇诺", "25.6"),
            )
        ),
    )

    data class CategoryInfo(val name: String, val goodsList: MutableList<GoodsInfo>) : NodeEntity<Unit, GoodsInfo> {
        override var xParentNodeEntity: Unit? = null
        override var xNodeGrade: Int? = null

        override fun getChildNodeEntityList(): MutableList<GoodsInfo> {
            return goodsList
        }

    }

    data class GoodsInfo(val image: Int, val name: String, val prick: String) : NodeEntity<CategoryInfo, Unit> {
        override var xParentNodeEntity: CategoryInfo? = null
        override var xNodeGrade: Int? = null

        override fun getChildNodeEntityList(): MutableList<Unit>? {
            return null
        }

        fun prickHtml(): CharSequence? {
            val split = prick.split(".")
            return if (split.isEmpty()) Html.fromHtml("<small>¥</small><big>${0}</big>")
            else if (split.size == 1) Html.fromHtml("<small>¥</small><big>${split[0]}</big>")
            else Html.fromHtml("<small>¥</small><big>${split[0]}.</big><small>${split[1]}</small>")
        }

    }
}