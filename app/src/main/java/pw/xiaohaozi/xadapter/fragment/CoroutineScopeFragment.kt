package pw.xiaohaozi.xadapter.fragment

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import coil.load
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentSelectedBinding
import pw.xiaohaozi.xadapter.databinding.ItemCameraBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageSelectedBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smart.ext.createLifecycleAdapter
import pw.xiaohaozi.xadapter.smart.ext.toAdapter
import pw.xiaohaozi.xadapter.smart.ext.withType

/**
 * item选择
 */
class CoroutineScopeFragment : Fragment() {
    val TAG = "CoroutineScopeFragment"
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


    @SuppressLint("SetTextI18n")
    private fun function2(): SmartAdapter<ViewBinding, Any?> {
        //泛型VB 确定布局文件，泛型D确定数据类型，回调函数中绑定数据
        val adapter = createLifecycleAdapter()
            .setOnItemSelectListener(
                payload = "select",
//                permittedTypes = arrayOf(java.lang.Integer::class.java)
                permittedTypes = arrayOf(Int.MIN_VALUE + 1)
            ) { data, position, index, fromUser ->
                binding.tvSelectedCount.text = "已选${getSelectedDatas().size}张"
            }.setOnSelectAllListener { selectedCache, isSelectedAll ->
                if (binding.ivSelectedAll.isSelected != isSelectedAll) {
                    binding.ivSelectedAll.isSelected = isSelectedAll
                    binding.tvSelectedAll.text = if (isSelectedAll) "全不选" else "全选"
                }
            }

            .withType<ItemCameraBinding, Any?> {

            }
            .setOnClickListener { holder, data, position, view ->
                Toast.makeText(requireContext(), "点击拍照", Toast.LENGTH_SHORT).show()
            }
            .withType<ItemImageSelectedBinding, Int> { (holder, data, position, payloads) ->
                Log.i(TAG, "function2: $data")
                if (!payloads.contains("select")) {
                    //模拟耗时操作，在滑动时能明显感觉到卡顿
                    //val bitmap = BitmapFactory.decodeResource(resources, data)
                    //holder.binding.ivImage.setImageBitmap(bitmap)
                    //使用协程，将耗时操作切换到其他线程
                    holder.launch(IO) {
                        val bitmap = BitmapFactory.decodeResource(resources, data)
                        withContext(Main) {
                            holder.binding.ivImage.setImageBitmap(bitmap)
                        }
                    }
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

    override fun onDestroy() {
        super.onDestroy()
    }

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

