package pw.xiaohaozi.xadapter.fragment.node

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.R
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
class RemoveAtNodeFragment : Fragment() {
    private lateinit var binding: FragmentNodeEditBinding

    private val adapter = function()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNodeEditBinding.inflate(inflater)
        binding.btnAddData.isVisible = false
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter
        adapter.refresh(dataList)
        return binding.root
    }


    fun function(): NodeAdapter<ViewBinding, NodeEntity<*, *>> {
        val adapter = nodeAdapter()
            .withType<ItemNodeEditBinding, NodeInfo1> { (holder, data) ->
                bindNode1(holder, data)
            }.setOnClickListener(R.id.btn_delete) { holder, data, position, view ->
                removeChild(data) //点击一级菜单，添加二级菜单数据
            }.withType<ItemNodeEditBinding, NodeInfo2> { (holder, data) ->
                bindNode2(holder, data)
            }.setOnClickListener(R.id.btn_delete) { holder, data, position, view ->
                removeNode(data)
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


    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo1>.removeChild(
        data: NodeInfo1
    ) {
        val index = adapter.source?.indexOf(data) ?: -1
        if (index != -1) {
            adapter.removeNodeAt(index)
        }
    }

    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo2>.removeNode(data: NodeInfo2) {
        val index = data.getParentNodeEntity()?.getChildNodeEntityList()?.indexOf(data) ?: -1
        if (index != -1) {
            adapter.removeChildNodeAt(data.getParentNodeEntity()!!, index)
        }
    }


    //提前预制两个根Node
    private val dataList = mutableListOf(
        NodeInfo1(
            "0", "Node0", mutableListOf(
                NodeInfo2(" 00", "ChildNode0"),
                NodeInfo2(" 00", "ChildNode1"),
                NodeInfo2(" 00", "ChildNode2"),
                NodeInfo2(" 00", "ChildNode3"),
                NodeInfo2(" 00", "ChildNode4"),
            )
        ),
        NodeInfo1(
            "1", "Node1", mutableListOf(
                NodeInfo2(" 10", "ChildNode0"),
                NodeInfo2(" 10", "ChildNode1"),
                NodeInfo2(" 10", "ChildNode2"),
                NodeInfo2(" 10", "ChildNode3"),
                NodeInfo2(" 10", "ChildNode4"),
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


