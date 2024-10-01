package pw.xiaohaozi.xadapter.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import coil.load
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentSelectedBinding
import pw.xiaohaozi.xadapter.databinding.ItemCameraBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageSelectedBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smart.ext.toAdapter
import pw.xiaohaozi.xadapter.smart.ext.withType

/**
 * 多布局
 */
class SelectFragment : Fragment() {
    val TAG = "SelectFragment"
    private lateinit var binding: FragmentSelectedBinding
    private val adapter = function2()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectedBinding.inflate(inflater)
        binding.llSelectedAll.setOnClickListener {
            if (adapter.isSelectAll())
                adapter.deselectAll()
            else
                adapter.selectAll()
        }
        binding.rvList.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.reset(list2)
    }

    /**
     * 方法1
     * 使用XAdapter拓展方法创建
     */
    @SuppressLint("SetTextI18n")
    private fun function1(): SmartAdapter<ItemImageSelectedBinding, Int> {
        //泛型VB 确定布局文件，泛型D确定数据类型，回调函数中绑定数据
        return createAdapter<ItemImageSelectedBinding, Int> { (holder, data) ->
            holder.binding.ivImage.load(data)
            val index = this.getSelectedIndex(data)
            if (index < 0) {
                holder.binding.tvSelectedIndex.text = ""
                holder.binding.tvSelectedIndex.setBackgroundResource(R.drawable.bg_not_selected)
            } else {
                holder.binding.tvSelectedIndex.text = "${index + 1}"
                holder.binding.tvSelectedIndex.setBackgroundResource(R.drawable.bg_selected_position)
            }

        }.setOnItemSelectStatusChanges { data, position, index ->

        }.setOnItemSelectListener { holder, data, position, index, fromUser ->
            binding.tvSelectedCount.text = "已选${getSelectedDatas().size}张"
        }.setOnSelectAllListener {
            binding.ivSelectedAll.isSelected = it
            binding.tvSelectedAll.text = if (it) "全不选" else "全选"
        }

    }

    private fun function2(): SmartAdapter<ViewBinding, Any?> {
        //泛型VB 确定布局文件，泛型D确定数据类型，回调函数中绑定数据
        val adapter = createAdapter()
            .setOnItemSelectListener(payload = "select") { holder, data, position, index, fromUser ->
                binding.tvSelectedCount.text = "已选${getSelectedDatas().size}张"
            }.setOnItemSelectStatusChanges { data, position, index ->
                Log.i(TAG, "setOnItemSelectedStatesChanges: $position -- $index")
            }.setOnSelectAllListener {
                binding.ivSelectedAll.isSelected = it
            }
//            .setMaxSelectCount(9)
//            .isAutoCancel(false)
//            .isAllowCancel(false)
            .withType<ItemCameraBinding, Any?> {

            }.setOnClickListener { holder, data, position, view ->
                Toast.makeText(requireContext(), "点击拍照", Toast.LENGTH_SHORT).show()
            }
//            .withType<ItemImageSelectedBinding, Int>(select = true) { holder, data, position, payloads ->
//                if (!payloads.contains("select")) {
//                    holder.binding.ivImage.load(data)
//                }
//                val index = this.adapter.getSelectedIndexAt(position)
//                if (index < 0) {
//                    holder.binding.tvSelectedIndex.text = ""
//                    holder.binding.tvSelectedIndex.setBackgroundResource(R.drawable.bg_not_selected)
//                } else {
//                    holder.binding.tvSelectedIndex.text = "${index + 1}"
//                    holder.binding.tvSelectedIndex.setBackgroundResource(R.drawable.bg_selected_position)
//                }
//
//            }
            .withType<ItemImageSelectedBinding, Int>(select = true) { (holder, data, position, payloads) ->
                if (!payloads.contains("select")) {
                    holder.binding.ivImage.load(data)
                }
                val index = this.adapter.getSelectedIndexAt(position)
                if (index < 0) {
                    holder.binding.tvSelectedIndex.text = ""
                    holder.binding.tvSelectedIndex.setBackgroundResource(R.drawable.bg_not_selected)
                } else {
                    holder.binding.tvSelectedIndex.text = "${index + 1}"
                    holder.binding.tvSelectedIndex.setBackgroundResource(R.drawable.bg_selected_position)
                }
            }
            .toAdapter()
        return adapter
    }


    private val list1 = arrayListOf(
        R.mipmap.snow1,
        R.mipmap.snow2,
        R.mipmap.snow3,
        R.mipmap.t1,
        R.mipmap.t2,
        R.mipmap.t3,
        R.mipmap.t4,
        R.mipmap.t5,
        R.mipmap.t6,
        R.mipmap.t7,
        R.mipmap.t8,
        R.mipmap.t9,
        R.mipmap.t10,
        R.mipmap.y1,
        R.mipmap.y2,
        R.mipmap.y3,
        R.mipmap.y4,
        R.mipmap.y5,
        R.mipmap.y6,
        R.mipmap.y7,
        R.mipmap.y8,
        R.mipmap.y9,
        R.mipmap.y10,


        )
    private val list2 = arrayListOf(
        null,
        R.mipmap.snow1,
        R.mipmap.snow2,
        R.mipmap.snow3,
        R.mipmap.t1,
        R.mipmap.t2,
        R.mipmap.t3,
        R.mipmap.t4,
        R.mipmap.t5,
        R.mipmap.t6,
        R.mipmap.t7,
        R.mipmap.t8,
        R.mipmap.t9,
        R.mipmap.t10,
        R.mipmap.y1,
        R.mipmap.y2,
        R.mipmap.y3,
        R.mipmap.y4,
        R.mipmap.y5,
        R.mipmap.y6,
        R.mipmap.y7,
        R.mipmap.y8,
        R.mipmap.y9,
        R.mipmap.y10,


        )


}

