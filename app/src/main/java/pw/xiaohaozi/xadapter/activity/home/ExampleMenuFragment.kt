package pw.xiaohaozi.xadapter.activity.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import pw.xiaohaozi.xadapter.activity.toEmptyActivity
import pw.xiaohaozi.xadapter.databinding.FragmentMenuBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeHeaderBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeTitleBinding
import pw.xiaohaozi.xadapter.exampleMenuList
import pw.xiaohaozi.xadapter.info.HomeInfo
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smart.ext.toAdapter
import pw.xiaohaozi.xadapter.smart.ext.withType


class ExampleMenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding

    private val adapter = createAdapter()
        .addHeader<ItemHomeHeaderBinding> { }
        .withType<ItemHomeTitleBinding, String>(isFixed = true) { (holder, data) ->
            holder.binding.tvTitle.text = data
        }
        .withType<ItemHomeBinding, HomeInfo> { (holder, data) ->
            holder.binding.data = data
        }
        .setOnClickListener { holder, data, position, view ->
            val clazz = data.clazz
            if (clazz == null) {
                Toast.makeText(requireContext(), "敬请期待", Toast.LENGTH_SHORT).show()
            } else
                toEmptyActivity(clazz, data.label, clazz.simpleName)
        }
        .toAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvList.adapter = adapter
        adapter.refresh(exampleMenuList)
    }
}