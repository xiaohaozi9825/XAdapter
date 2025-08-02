package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeBinding
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter


class CreateNodeFragment : Fragment() {
    private lateinit var binding: FragmentRecyclerBinding
    val adapter = nodeAdapter<ItemNodeBinding, NodeInfo> { (holder, data) ->
        holder.binding.tvContent.text = data.name
    }.setOnClickListener { holder, data, position, view ->
        if (data.isExpanded()) collapse(position, true)
        else expand(position)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclerBinding.inflate(inflater)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleView.adapter = adapter
        adapter.refresh(list)

    }

    val list = mutableListOf(
        NodeInfo("一级菜单1", mutableListOf( NodeInfo("        二级级菜单11"),NodeInfo("        二级级菜单12"))),
        NodeInfo("一级菜单2", mutableListOf( NodeInfo("        二级级菜单21"),NodeInfo("        二级级菜单22"))),
        NodeInfo("一级菜单3", mutableListOf( NodeInfo("        二级级菜单31"),NodeInfo("        二级级菜单32"),NodeInfo("        二级级菜单33"))),
        NodeInfo("一级菜单4", mutableListOf( NodeInfo("        二级级菜单41"),NodeInfo("        二级级菜单42"))),
    )

    data class NodeInfo(val name: String, val child: MutableList<NodeInfo>? = null) : NodeEntity<NodeInfo?, NodeInfo>,
        ExpandedNodeEntity {
        override var xParentNodeEntity: NodeInfo? = null
        override var xNodeGrade: Int? = null
        override var xIsExpanded: Boolean = true

        override fun getChildNodeEntityList(): MutableList<NodeInfo>? {
            return child
        }
    }

}

