package pw.xiaohaozi.xadapter.fragment.node

import androidx.recyclerview.widget.LinearLayoutManager
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeBinding
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter


class CreateNodeFragment : VBFragment<FragmentRecyclerBinding>() {
    val adapter = nodeAdapter<ItemNodeBinding, NodeInfo> { (holder, data) ->
        holder.binding.tvContent.text = data.name
    }

    override fun FragmentRecyclerBinding.initView() {
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter
        adapter.refresh(list)
    }

    val list = mutableListOf(
        NodeInfo("一级菜单1", mutableListOf( NodeInfo("        二级级菜单11"), NodeInfo("        二级级菜单12"))),
        NodeInfo("一级菜单2", mutableListOf( NodeInfo("        二级级菜单21"), NodeInfo("        二级级菜单22"))),
        NodeInfo("一级菜单3", mutableListOf( NodeInfo("        二级级菜单31"),
            NodeInfo("        二级级菜单32"),
            NodeInfo("        二级级菜单33")
        )),
        NodeInfo("一级菜单4", mutableListOf( NodeInfo("        二级级菜单41"), NodeInfo("        二级级菜单42"))),
    )

    data class NodeInfo(val name: String, val child: MutableList<NodeInfo>? = null) : NodeEntity<NodeInfo?, NodeInfo>{
        override var xParentNodeEntity: NodeInfo? = null
        override var xNodeGrade: Int? = null

        override fun getChildNodeEntityList(): MutableList<NodeInfo>? {
            return child
        }
    }

}

