package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeTitleBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageSelectedBinding
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smart.ext.toAdapter
import pw.xiaohaozi.xadapter.smart.ext.withType

/**
 * item选择
 */
class GroupFragment : Fragment() {
    val TAG = "GroupFragment"
    private lateinit var binding: FragmentRecyclerBinding
    private val adapter = createAdapter()
        .withType<ItemHomeTitleBinding, String>(isFixed = true) {
            it.holder.binding.tvTitle.text = it.data
        }
        .withType<ItemImageSelectedBinding, Int> {
            it.holder.binding.ivImage.load(it.data)
            it.holder.binding.tvSelectedIndex.isVisible = false
        }
        .toAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclerBinding.inflate(inflater)
        binding.recycleView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recycleView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.refresh(list)
    }


    private val list = arrayListOf(
        "2024-03-24",
        R.mipmap.snow1,
        R.mipmap.snow2,
        R.mipmap.snow3,
        "2024-05-18",
        R.mipmap.t1,
        R.mipmap.t2,
        R.mipmap.t3,
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
        R.mipmap.y5,
        R.mipmap.y6,
        R.mipmap.y7,
        R.mipmap.y8,
        R.mipmap.y9,
        R.mipmap.y10,

        )

}

