package pw.xiaohaozi.xadapter.fragment.example

import android.graphics.Color
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentTagBinding
import pw.xiaohaozi.xadapter.databinding.ItemTagBinding
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.smart.ext.createAdapter

class TagFragment : VBFragment<FragmentTagBinding>() {
    val adapter = createAdapter<ItemTagBinding, String> {
        it.binding.root.apply {
            val selected = isSelected(it.data)
            text = it.data
            setTextColor(if (selected) Color.WHITE else resources.getColor(R.color.theme))
            isSelected = selected
        }
    }
        .setOnClickListener { holder, data, position, view ->
            setSelectAt(position, !isSelected(data))
        }
//        .setOnItemSelectListener { data, position, index, fromUser ->
//
//        }

    override fun FragmentTagBinding.initView() {
        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW // 横向排列
            justifyContent = JustifyContent.FLEX_START // 左对齐（可改居中/右对齐）
        }
        recycleView.layoutManager = flexboxLayoutManager
        recycleView.adapter = adapter
        adapter.refresh(list)
    }

    val list = arrayListOf(
        "RecyclerView",
        "流式布局",
        "XAdapter",
        "Android",
        "Java",
        "Kotlin",
        "Jetpack",
        "MVVM",
        "自定义View",
        "性能优化",
        "网络请求",
        "Glide",
        "OkHttp",
        "长文本标签测试",
        "RxJava",
        "Flutter",
        "鸿蒙开发",
        "VUE",
        "UniApp",
        "小米",
        "vivo",
        "OPPO",
        "华为",
        "iPhone",

        )
}