package pw.xiaohaozi.xadapter.fragment.example

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentGoodsBinding
import pw.xiaohaozi.xadapter.databinding.ItemCategoryNodeBinding
import pw.xiaohaozi.xadapter.databinding.ItemGoodsBinding
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.utils.load


class MultipleCategoryFragment : Fragment() {
    private lateinit var binding: FragmentGoodsBinding
    private val TAG = "MultipleCategoryFragment"
    private var selectedCategory: CategoryInfo? = null

    //左边分类列表适配器
    @SuppressLint("SetTextI18n")
    private val categoryAdapter = nodeAdapter<ItemCategoryNodeBinding, CategoryInfo> { (holder, data, position, player) ->
        holder.binding.tvName.text = data.name
        if (data.getNodeEntityGrade() == 1) {
            holder.binding.tvName.textSize = 14f
        } else {
            holder.binding.tvName.textSize = 13f
        }
        //根据选中状态，改变item背景颜色和文字颜色
        if (selectedCategory == data) {
            holder.binding.root.setBackgroundColor(Color.WHITE)
            holder.binding.tvName.setTextColor(requireContext().resources.getColor(R.color.theme))
        } else {
            holder.binding.root.setBackgroundColor(Color.TRANSPARENT)
            if (data.getNodeEntityGrade() == 1) {
                holder.binding.tvName.setTextColor(Color.parseColor("#FF000000"))
            } else {
                holder.binding.tvName.setTextColor(Color.parseColor("#FF666666"))
            }
        }
        holder.binding.ivArrow.isInvisible = data.childCategoryList == null
        holder.binding.ivArrow.rotation = if (data.isExpanded()) 0f else -90f
    }.setOnClickListener { holder, data, position, view ->
        //如果点击根Node，则收起其他node
        if (data.getParentNodeEntity() == null && !data.isExpanded()) {
            val collapsePosition = source?.indexOfFirst { it.isExpanded() } ?: -1
            if (collapsePosition != -1) {
                collapse(collapsePosition)
            }
        }
        if (data.childCategoryList == null) {//如果没有二级分类，则直接刷新商品列表
            val oldSelectedPosition = getDataList().indexOf(selectedCategory)
            val newSelectedPosition = getDataList().indexOf(data)//由于收起后，当前item位置可能会发生变化，因此不能直接使用position
            selectedCategory = data
            //取消旧的选中状态
            if (oldSelectedPosition != -1) notifyItemChanged(oldSelectedPosition)
            //设置新的选中状态
            if (newSelectedPosition != -1) notifyItemChanged(newSelectedPosition)
            //更新商品列表
            goodsAdapter.refresh(data.goodsList ?: mutableListOf())
        } else {//如果有二级分类，则展开二级分类，并将二级分类中第一个置为选中状态
            if (!data.isExpanded()) {
                val expandPosition = source?.indexOf(data) ?: -1
                if (expandPosition != -1) {
                    //展开二级分类
                    expand(expandPosition)

                    val oldSelectedPosition = getDataList().indexOf(selectedCategory)
                    val newSelectedCategory = data.childCategoryList.firstOrNull()
                    selectedCategory = newSelectedCategory
                    val newSelectedPosition = getDataList().indexOf(newSelectedCategory)
                    //取消旧的选中状态
                    if (oldSelectedPosition != -1) notifyItemChanged(oldSelectedPosition)
                    //设置新的选中状态
                    if (newSelectedPosition != -1) notifyItemChanged(newSelectedPosition)
                    //更新商品列表
                    goodsAdapter.refresh(newSelectedCategory?.goodsList ?: mutableListOf())

                }
            }

        }
    }


    //右边商品列表适配器
    @SuppressLint("SetTextI18n")
    private val goodsAdapter = createAdapter<ItemGoodsBinding, GoodsInfo> { (holder, data) ->
        holder.binding.tvPrick.text = data.prickHtml()
        holder.binding.tvName.text = data.name
        holder.binding.ivImage.load(data.image, 8f)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoodsBinding.inflate(inflater)
        binding.category.isVisible = false
        binding.rvCategory.adapter = categoryAdapter
        binding.rvGoods.adapter = goodsAdapter
        categoryAdapter.refresh(dataList)
        //分类列表默认选中第一个
        val categoryInfo = dataList.firstOrNull()
        selectedCategory = if (categoryInfo?.childCategoryList != null) {
            categoryAdapter.expand(0)
            categoryInfo.childCategoryList.firstOrNull()
        } else {
            categoryInfo
        }
        if (selectedCategory != null) {
            categoryAdapter.notifyItemChanged(categoryAdapter.getDataList().indexOf(selectedCategory))
            goodsAdapter.refresh(selectedCategory!!.goodsList!!)
        }
        return binding.root
    }

    val dataList = mutableListOf(
        CategoryInfo(
            "今日推荐", mutableListOf(
                CategoryInfo(
                    "轻乳茶", goodsList = mutableListOf(
                        GoodsInfo(R.mipmap.snow1, "鲜萃轻轻红茶", "19.2"),
                        GoodsInfo(R.mipmap.snow2, "鲜萃轻轻茉莉", "19.2"),
                    )
                ),
                CategoryInfo(
                    "果蔬茶", goodsList = mutableListOf(
                        GoodsInfo(R.mipmap.snow3, "小桑葚果蔬茶", "17.98"),
                        GoodsInfo(R.mipmap.t1, "羽衣轻体果蔬茶", "19.2"),
                    )
                ),
            )
        ),
        CategoryInfo(
            "特价商品", mutableListOf(
                CategoryInfo(
                    "非咖/果茶", goodsList = mutableListOf(
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
                    "美式家族", goodsList = mutableListOf(
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
            )
        ),

        CategoryInfo(
            "风味拿铁", goodsList = mutableListOf(
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
            "咖啡轻食", goodsList = mutableListOf(
                GoodsInfo(R.mipmap.snow3, "拿铁 早餐", "17.98"),
                GoodsInfo(R.mipmap.y4, "美式 早餐", "17.98"),
                GoodsInfo(R.mipmap.y5, "轻享·下午茶", "17.98"),
            )
        ),
        CategoryInfo(
            "大师咖啡", goodsList = mutableListOf(
                GoodsInfo(R.mipmap.t1, "拿铁", "20.88"),
                GoodsInfo(R.mipmap.t2, "焦糖玛朵奇", "25.6"),
                GoodsInfo(R.mipmap.t3, "香草拿铁", "21.12"),
                GoodsInfo(R.mipmap.t4, "精萃奥瑞白", "25.6"),
                GoodsInfo(R.mipmap.t5, "卡布奇诺", "25.6"),
            )
        ),
    )

    data class CategoryInfo(
        val name: String,
        val childCategoryList: MutableList<CategoryInfo>? = null,
        val goodsList: MutableList<GoodsInfo>? = null,
    ) : NodeEntity<CategoryInfo, CategoryInfo>, ExpandedNodeEntity {
        override var xParentNodeEntity: CategoryInfo? = null
        override var xNodeGrade: Int? = null
        override var xIsExpanded: Boolean? = null
        override fun getChildNodeEntityList(): MutableList<CategoryInfo>? {
            return childCategoryList
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