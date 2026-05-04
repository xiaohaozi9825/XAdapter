package pw.xiaohaozi.xadapter.fragment.node

import android.graphics.Typeface
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentNodeEditBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeEditBinding
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder


/**
 * 单布局
 */
class RemovePositionNodeFragment : VBFragment<FragmentNodeEditBinding>() {

    private val adapter = function()

    override fun FragmentNodeEditBinding.initView() {
        binding.btnAddData.isVisible = false
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter
        adapter.refresh(dataList)
    }


    fun function(): NodeAdapter<ViewBinding, NodeEntity<*, *>> {
        val adapter = nodeAdapter()
            .withType<ItemNodeEditBinding, NodeInfo1> { (holder, data) ->
                bindNode1(holder, data)
            }
            .withType<ItemNodeEditBinding, NodeInfo2> { (holder, data) ->
                bindNode2(holder, data)
            }
            .toAdapter()
            .setOnClickListener(R.id.btn_delete) { holder, data, position, view ->
                removeNodePosition(position)
            }
        return adapter
    }

    private fun bindNode1(holder: XHolder<ItemNodeEditBinding>, data: NodeInfo1) {
        holder.binding.apply {
            tvContent.text = data.no + "、" + data.text
            tvContent.setTypeface(Typeface.DEFAULT_BOLD)
            btnAdd.isVisible = false
            btnEdit.isVisible = false
            btnDelete.isVisible = true
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
            btnDelete.isVisible = true
            ivArrow.isVisible = false
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
        NodeInfo1(
            "1", "Node1", mutableListOf(
                NodeInfo2(" 10", "ChildNode0"),
                NodeInfo2(" 11", "ChildNode1"),
                NodeInfo2(" 12", "ChildNode2"),
                NodeInfo2(" 13", "ChildNode3"),
                NodeInfo2(" 14", "ChildNode4"),
            )
        ),
        NodeInfo1(
            "2", "Node2", mutableListOf(
                NodeInfo2(" 20", "ChildNode0"),
                NodeInfo2(" 21", "ChildNode1"),
                NodeInfo2(" 22", "ChildNode2"),
                NodeInfo2(" 23", "ChildNode3"),
                NodeInfo2(" 24", "ChildNode4"),
            )
        ),
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


