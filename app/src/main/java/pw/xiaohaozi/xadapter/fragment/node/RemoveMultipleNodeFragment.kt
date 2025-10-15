package pw.xiaohaozi.xadapter.fragment.node

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.databinding.FragmentNodeEditBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeEditBinding
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.node.NodeProvider
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder


/**
 * 单布局
 */
class RemoveMultipleNodeFragment : Fragment() {
    private lateinit var binding: FragmentNodeEditBinding

    private val adapter = function()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNodeEditBinding.inflate(inflater)
        binding.btnAddData.text = "删除前2个一级节点"
        binding.btnAddData.setOnClickListener {
            if ((adapter.source?.size ?: 0) >= 2) {
                adapter.removeNode(0, 2)
            } else {
                Toast.makeText(requireContext(), "数据不足2条，无法删除", Toast.LENGTH_SHORT).show()
            }
        }
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter
        adapter.refresh(dataList)
        return binding.root
    }


    fun function(): NodeAdapter<ViewBinding, NodeEntity<*, *>> {
        val adapter = nodeAdapter()
            .withType<ItemNodeEditBinding, NodeInfo1> { (holder, data) ->
                bindNode1(holder, data)
            }.setOnClickListener { holder, data, position, view ->
                removeChild(data)
            }.withType<ItemNodeEditBinding, NodeInfo2> { (holder, data) ->
                bindNode2(holder, data)
            }
            .toAdapter()
        return adapter
    }

    private fun bindNode1(holder: XHolder<ItemNodeEditBinding>, data: NodeInfo1) {
        holder.binding.apply {
            tvContent.text = data.no + "、" + data.text
            tvContent.setTypeface(Typeface.DEFAULT_BOLD)
            btnAdd.isVisible = false
            btnEdit.isVisible = false
            btnDelete.isVisible = true
            btnDelete.text = "删除前2个元素"
            ivArrow.isVisible = false
            ivArrow.rotation = if (data.isExpanded()) 0f else -90f
        }
    }

    private fun bindNode2(holder: XHolder<ItemNodeEditBinding>, data: NodeInfo2) {
        holder.binding.apply {
            tvContent.text = data.no + "、" + data.text
            tvContent.setTypeface(Typeface.DEFAULT)
            btnAdd.isVisible = false
            btnEdit.isVisible = false
            btnDelete.isVisible = false
            ivArrow.isVisible = false
        }
    }


    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo1>.removeChild(
        data: NodeInfo1
    ) {
        if (data.getChildNodeEntityList().size >= 2) {
            adapter.removeChildNode(data, 0, 2)
        } else {
            Toast.makeText(requireContext(), "数据不足2条，无法删除", Toast.LENGTH_SHORT).show()
        }
    }

    //提前预制两个根Node
    private val dataList = mutableListOf(
        NodeInfo1(
            "0", "Node0", mutableListOf(
                NodeInfo2(" 00", "ChildNode0"),
                NodeInfo2(" 01", "ChildNode1"),
                NodeInfo2(" 02", "ChildNode2"),
                NodeInfo2(" 03", "ChildNode3"),
                NodeInfo2(" 04", "ChildNode4"),
            )
        ),
        NodeInfo1("1", "Node1", mutableListOf(NodeInfo2(" 10", "ChildNode0"))),
        NodeInfo1("2", "Node2", mutableListOf(NodeInfo2(" 20", "ChildNode0"))),
        NodeInfo1("3", "Node3", mutableListOf(NodeInfo2(" 30", "ChildNode0"))),
        NodeInfo1("4", "Node4", mutableListOf(NodeInfo2(" 40", "ChildNode0"))),
        NodeInfo1("5", "Node5", mutableListOf(NodeInfo2(" 50", "ChildNode0"))),
        NodeInfo1("6", "Node6", mutableListOf(NodeInfo2(" 60", "ChildNode0"))),
    )

    class NodeInfo1(val no: String, var text: String, private val childList: MutableList<NodeInfo2>) :
        NodeEntity<Unit, NodeInfo2>, ExpandedNodeEntity {
        override var xParentNodeEntity: Unit? = null
        override var xNodeGrade: Int? = null
        override var xIsExpanded: Boolean? = true

        override fun getChildNodeEntityList(): MutableList<NodeInfo2> {
            return childList
        }

        override fun toString(): String {
            return no
        }
    }

    class NodeInfo2(val no: String, val text: String) :
        NodeEntity<NodeInfo1, String>, ExpandedNodeEntity {
        override var xParentNodeEntity: NodeInfo1? = null
        override var xNodeGrade: Int? = null
        override var xIsExpanded: Boolean? = true

        override fun getChildNodeEntityList(): MutableList<String>? {
            return null
        }

        override fun toString(): String {
            return no
        }
    }


}


