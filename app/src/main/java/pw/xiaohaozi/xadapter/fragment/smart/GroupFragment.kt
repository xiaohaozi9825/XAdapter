package pw.xiaohaozi.xadapter.fragment.smart

import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeTitleBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageAutoHeightBinding
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.smart.ext.createAdapter


/**
 * item选择
 */
class GroupFragment : VBFragment<FragmentRecyclerBinding>() {
    val TAG = "GroupFragment"
    private val adapter = createAdapter()
        .withType<ItemHomeTitleBinding, String>(isFixed = true) {
            it.holder.binding.tvTitle.text = it.data
        }
        .withType<ItemImageAutoHeightBinding, Int> {
            it.holder.binding.ivImage.setImageResource(it.data)
        }
        .toAdapter()


    override fun FragmentRecyclerBinding.initView() {
        binding.recycleView.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        binding.recycleView.adapter = adapter
        adapter.refresh(list)
    }


    private val list = arrayListOf(
        "2024-03-24",
        R.mipmap.snow1,
        R.mipmap.t3,
        R.mipmap.y5,
        R.mipmap.snow2,
        R.mipmap.snow3,
        "2024-05-18",
        R.mipmap.t1,
        R.mipmap.t2,
        R.mipmap.t4,
        R.mipmap.t5,
        R.mipmap.t6,
        R.mipmap.t7,
        R.mipmap.t8,
        R.mipmap.t9,
        "2024-10-04",
        R.mipmap.t10,
        R.mipmap.y1,
        R.mipmap.y2,
        R.mipmap.y3,
        R.mipmap.y4,
        R.mipmap.y6,
        R.mipmap.y7,
        R.mipmap.y8,
        R.mipmap.y9,
        R.mipmap.y10,

        )

}

