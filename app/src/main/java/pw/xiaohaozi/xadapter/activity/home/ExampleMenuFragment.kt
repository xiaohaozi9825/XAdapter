package pw.xiaohaozi.xadapter.activity.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.activity.toEmptyActivity
import pw.xiaohaozi.xadapter.databinding.FragmentMenuBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeHeaderBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeTitleBinding
import pw.xiaohaozi.xadapter.exampleMenuList
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.info.HomeInfo
import pw.xiaohaozi.xadapter.smart.ext.createAdapter


class ExampleMenuFragment : VBFragment<FragmentMenuBinding>() {

    private val adapter = createAdapter()
        .addHeader<ItemHomeHeaderBinding> { holder, _ -> holder.binding.ivPoster.setImageResource(R.mipmap.image_poster3) }
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


    override fun FragmentMenuBinding.initView() {
        rvList.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        rvList.adapter = adapter
        adapter.refresh(exampleMenuList)
    }
}